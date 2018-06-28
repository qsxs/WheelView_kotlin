package lihb.library.wheelview

import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.GradientDrawable.Orientation
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.Interpolator
import android.widget.LinearLayout
import java.util.*

class WheelView : View {
    companion object {
        private const val DEF_DIVING_COLOR = Color.GRAY
        private const val ITEM_OFFSET_PERCENT = 0
        private const val PADDING = 10
        private const val DEF_VISIBLE_ITEMS = 5
    }

    private val gradualColors = intArrayOf(0x70FFFFFF, 0x30FFFFFF, 0)

    var isGradual = true
    var divingColor = DEF_DIVING_COLOR
    var divingWidth = 1
    private var isCyclic = false
    var visibleItems = DEF_VISIBLE_ITEMS

    // Wheel Values
    private var currentItem = 0

    // Item height
    private var itemHeight = 0

    // Shadows drawables
    private var topShadow: GradientDrawable? = null
    private var bottomShadow: GradientDrawable? = null

    // Scrolling
    private var scroller: WheelScroller? = null
    private var isScrollingPerformed: Boolean = false
    private var scrollingOffset: Int = 0

    // Items layout
    private var itemsLayout: LinearLayout? = null

    // The number of first item in layout
    private var firstItem: Int = 0

    var adapter: WheelViewAdapter? = null
        set(viewAdapter) {
            if (this.adapter != null) {
                this.adapter!!.unregisterDataSetObserver(dataObserver)
            }
            field = viewAdapter
            if (field != null) {
                field!!.wheelView = this
                field!!.registerDataSetObserver(dataObserver)
            }

            invalidateWheel(true)
        }

    // Recycle
    private val recycle = WheelRecycle(this)

    // Listeners
    private val changingListeners = LinkedList<OnWheelChangedListener>()
    private val scrollingListeners = LinkedList<OnWheelScrollListener>()

    // Scrolling listener
    private val scrollingListener = object : WheelScroller.ScrollingListener {
        override fun onStarted() {
            isScrollingPerformed = true
            notifyScrollingListenersAboutStart()
        }

        override fun onScroll(distance: Int) {
            doScroll(distance)

            val height = height
            if (scrollingOffset > height) {
                scrollingOffset = height
                scroller!!.stopScrolling()
            } else if (scrollingOffset < -height) {
                scrollingOffset = -height
                scroller!!.stopScrolling()
            }
        }

        override fun onFinished() {
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd()
                isScrollingPerformed = false
            }

            scrollingOffset = 0
            invalidate()
        }

        override fun onJustify() {
            if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                scroller!!.scroll(scrollingOffset, WheelScroller.SCROLLING_DURATION)
            }
        }
    }

    // Adapter listener
    private val dataObserver = object : DataSetObserver() {
        override fun onChanged() {
            invalidateWheel(false)
        }

        override fun onInvalidated() {
            invalidateWheel(true)
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    /**
     * Set the the specified scrolling interpolator
     *
     * @param interpolator the interpolator
     */
    fun setInterpolator(interpolator: Interpolator) {
        scroller!!.setInterpolator(interpolator)
    }

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    fun addChangingListener(@Nullable listener: (WheelView, Int, Int) -> Int) {
        addChangingListener(object : OnWheelChangedListener {
            override fun onChanged(wheel: WheelView, oldValue: Int, newValue: Int) {
                listener.invoke(wheel, oldValue, newValue)
            }

        })
    }

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    fun addChangingListener(@Nullable listener: OnWheelChangedListener) {
        changingListeners.add(listener)
    }

    /**
     * Removes wheel changing listener
     *
     * @param listener the listener
     */
    fun removeChangingListener(listener: OnWheelChangedListener) {
        changingListeners.remove(listener)
    }

    /**
     * Adds wheel scrolling listener
     *
     * @param listener the listener
     */
    fun addScrollingListener(listener: OnWheelScrollListener) {
        scrollingListeners.add(listener)
    }

    /**
     * Removes wheel scrolling listener
     *
     * @param listener the listener
     */
    fun removeScrollingListener(listener: OnWheelScrollListener) {
        scrollingListeners.remove(listener)
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
     *
     * @return true if wheel is cyclic
     */
    fun isCyclic(): Boolean {
        return isCyclic
    }

    /**
     * Set wheel cyclic flag
     *
     * @param isCyclic the flag to set
     */
    fun setCyclic(isCyclic: Boolean) {
        this.isCyclic = isCyclic
        invalidateWheel(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        buildViewForMeasuring()

        val width = calculateLayoutWidth(widthSize, widthMode)

        var height: Int
        if (heightMode == View.MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = getDesiredHeight(itemsLayout)

            if (heightMode == View.MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize)
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        layout(r - l, b - t)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (adapter != null && adapter!!.getItemsCount() > 0) {
            updateView()

            drawItems(canvas)
            if (isGradual) drawShadows(canvas)
            drawCenterRect(canvas)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || adapter == null) {
            return true
        }

        when (event.action) {
            MotionEvent.ACTION_MOVE -> if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_UP -> if (!isScrollingPerformed && adapter != null) {
                var distance = event.y.toInt() - height / 2
                if (distance > 0) {
                    distance += getItemHeight() / 2
                } else {
                    distance -= getItemHeight() / 2
                }
                val items = distance / getItemHeight()
                if (isValidItemIndex(currentItem + items)) {
                    var index = currentItem + items
                    if (index < 0 && isCyclic) {
                        index = adapter!!.getItemsCount() + items
                    }
                    notifyClickListeners(index, items == 0)
                }
            }
        }

        return scroller!!.onTouchEvent(event)
    }

    /**
     * Stops scrolling
     */
    fun stopScrolling() {
        scroller!!.stopScrolling()
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index    the item index
     * @param animated the animation flag
     */
    internal fun setCurrentItem(index: Int, animated: Boolean, animationDuration: Int) {
        var indexFinal = index
        if (adapter == null || adapter!!.getItemsCount() == 0) {
            return  // throw?
        }

        val itemCount = adapter!!.getItemsCount()
        if (indexFinal < 0 || indexFinal >= itemCount) {
            if (isCyclic) {
                while (index < 0) {
                    indexFinal += itemCount
                }
                indexFinal %= itemCount
            } else {
                return  // throw?
            }
        }
        if (indexFinal != currentItem) {
            if (animated) {
                var itemsToScroll = indexFinal - currentItem
                if (isCyclic) {
                    val scroll = itemCount + Math.min(indexFinal, currentItem) - Math.max(indexFinal, currentItem)
                    if (scroll < Math.abs(itemsToScroll)) {
                        itemsToScroll = if (itemsToScroll < 0) scroll else -scroll
                    }
                }
                scroll(itemsToScroll, animationDuration)
            } else {
                scrollingOffset = 0

                val old = currentItem
                currentItem = indexFinal
                if (!isScrollingPerformed) {
                    notifyOnSelectedListener(currentItem)
                }
                notifyChangingListeners(old, currentItem)

                invalidate()
            }
        }
    }

    /**
     * Invalidates wheel
     *
     * @param clearCaches if true then cached views will be clear
     */
    internal fun invalidateWheel(clearCaches: Boolean) {
        if (clearCaches) {
            recycle.clearAll()
            if (itemsLayout != null) {
                itemsLayout!!.removeAllViews()
            }
            scrollingOffset = 0
        } else if (itemsLayout != null) {
            // cache all items
            recycle.recycleItems(itemsLayout!!, firstItem, ItemsRange())
        }

        invalidate()
    }

    /**
     * Initializes class data
     *
     * @param context the context
     * @param attrs
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        scroller = WheelScroller(context, scrollingListener)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.WheelView, 0, 0)
            isGradual = a.getBoolean(R.styleable.WheelView_gradual, isGradual)
            divingColor = a.getColor(R.styleable.WheelView_diving_color, divingColor)
            isCyclic = a.getBoolean(R.styleable.WheelView_cyclic, isCyclic)
            visibleItems = a.getInt(R.styleable.WheelView_visible_items, visibleItems)
            divingWidth = a.getDimensionPixelSize(R.styleable.WheelView_diving_width, divingWidth)
            a.recycle()
        }
    }

    /**
     * Calculates range for wheel items
     *
     * @return the items range
     */
    private fun getItemsRange(): ItemsRange {
        if (getItemHeight() == 0) {
            return ItemsRange(0, 0)
        }

        var first = currentItem
        var count = 1

        while (count * getItemHeight() < height) {
            first--
            count += 2
        }

        if (scrollingOffset != 0) {
            if (scrollingOffset > 0) {
                first--
            }
            count++
            val emptyItems = scrollingOffset / getItemHeight()
            first -= emptyItems
            count += Math.asin(emptyItems.toDouble()).toInt()
        }
        return ItemsRange(first, count)
    }

    /**
     * Initializes resources
     */
    private fun initResourcesIfNecessary() {
        if (topShadow == null) {
            topShadow = GradientDrawable(Orientation.TOP_BOTTOM, gradualColors)
        }

        if (bottomShadow == null) {
            bottomShadow = GradientDrawable(Orientation.BOTTOM_TOP, gradualColors)
        }
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout the source layout
     * @return the desired layout height
     */
    private fun getDesiredHeight(layout: LinearLayout?): Int {
        if (layout?.getChildAt(0) != null) {
            itemHeight = layout.getChildAt(0).measuredHeight
        }

        val desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50

        return Math.max(desired, suggestedMinimumHeight)
    }

    /**
     * Returns height of wheel item
     *
     * @return the item height
     */
    private fun getItemHeight(): Int {
        if (itemHeight != 0) {
            return itemHeight
        }

        if (itemsLayout != null && itemsLayout!!.getChildAt(0) != null) {
            itemHeight = itemsLayout!!.getChildAt(0).height
            return itemHeight
        }

        return height / visibleItems
    }

    /**
     * Scrolls the wheel
     *
     * @param delta the scrolling value
     */
    private fun doScroll(delta: Int) {
        scrollingOffset += delta

        val itemHeight = getItemHeight()
        var count = scrollingOffset / itemHeight

        var pos = currentItem - count
        val itemCount = adapter!!.getItemsCount()

        var fixPos = scrollingOffset % itemHeight
        if (Math.abs(fixPos) <= itemHeight / 2) {
            fixPos = 0
        }
        if (isCyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--
                count++
            } else if (fixPos < 0) {
                pos++
                count--
            }
            // fix position by rotating
            while (pos < 0) {
                pos += itemCount
            }
            pos %= itemCount
        } else {
            //
            if (pos < 0) {
                count = currentItem
                pos = 0
            } else if (pos >= itemCount) {
                count = currentItem - itemCount + 1
                pos = itemCount - 1
            } else if (pos > 0 && fixPos > 0) {
                pos--
                count++
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++
                count--
            }
        }

        val offset = scrollingOffset
        if (pos != currentItem) {
            setCurrentItem(pos, false, 0)
        } else {
            invalidate()
        }

        // update offset
        scrollingOffset = offset - count * itemHeight
        if (scrollingOffset > height) {
            scrollingOffset = scrollingOffset % height + height
        }
    }

    /**
     * Scroll the wheel
     *
     * @param time scrolling duration
     */
    private fun scroll(itemsToScroll: Int, time: Int) {
        val distance = itemsToScroll * getItemHeight() - scrollingOffset
        scroller!!.scroll(distance, time)
    }

    /**
     * Calculates control width and creates text layouts
     *
     * @param widthSize the input layout width
     * @param mode      the layout mode
     * @return the calculated control width
     */
    private fun calculateLayoutWidth(widthSize: Int, mode: Int): Int {

        // TODO: make it static
        itemsLayout!!.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        itemsLayout!!.measure(View.MeasureSpec.makeMeasureSpec(widthSize, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        var width = itemsLayout!!.measuredWidth

        if (mode == View.MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            width += 2 * PADDING

            // Check against our minimum width
            width = Math.max(width, suggestedMinimumWidth)

            if (mode == View.MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize
            }
        }

        itemsLayout!!.measure(View.MeasureSpec.makeMeasureSpec(width - 2 * PADDING, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        return width
    }

    /**
     * Rebuilds wheel items if necessary. Caches all unused items.
     *
     * @return true if items are rebuilt
     */
    private fun rebuildItems(): Boolean {
        var updated: Boolean
        val range = getItemsRange()
        if (itemsLayout != null) {
            val first = recycle.recycleItems(itemsLayout!!, firstItem, range)
            updated = firstItem != first
            firstItem = first
        } else {
            createItemsLayout()
            updated = true
        }

        if (!updated) {
            updated = firstItem != range.first || itemsLayout!!.childCount != range.count
        }

        if (firstItem > range.first && firstItem <= range.last) {
            for (i in firstItem - 1 downTo range.first) {
                if (!addViewItem(i, true)) {
                    break
                }
                firstItem = i
            }
        } else {
            firstItem = range.first
        }

        var first = firstItem
        for (i in itemsLayout!!.childCount until range.count) {
            if (!addViewItem(firstItem + i, false) && itemsLayout!!.childCount == 0) {
                first++
            }
        }
        firstItem = first

        return updated
    }

    /**
     * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
     */
    private fun updateView() {
        if (rebuildItems()) {
            calculateLayoutWidth(width, View.MeasureSpec.EXACTLY)
            layout(width, height)
        }
    }

    /**
     * Creates item layouts if necessary
     */
    private fun createItemsLayout() {
        if (itemsLayout == null) {
            itemsLayout = LinearLayout(context)
            itemsLayout!!.orientation = LinearLayout.VERTICAL
        }
    }

    /**
     * Draws rect for current value
     *
     * @param canvas the canvas for drawing
     */
    private fun drawCenterRect(canvas: Canvas) {
        val center = height / 2
        val offset = (getItemHeight() / 2 * 1.1).toInt()
        //画中间两条线
        val paint = Paint()
        paint.color = divingColor
        // 设置线宽
        paint.strokeWidth = divingWidth.toFloat()
        // 绘制上边直线
        canvas.drawLine(0f, (center - offset).toFloat(), width.toFloat(), (center - offset).toFloat(), paint)
        // 绘制下边直线
        canvas.drawLine(0f, (center + offset).toFloat(), width.toFloat(), (center + offset).toFloat(), paint)
    }

    /**
     * Draws shadows on top and bottom of control
     *
     * @param canvas the canvas for drawing
     */
    private fun drawShadows(canvas: Canvas) {
        val height = ((height - getItemHeight()) * 0.7).toInt()
        initResourcesIfNecessary()
        topShadow!!.setBounds(0, 0, width, height)
        topShadow!!.draw(canvas)

        bottomShadow!!.setBounds(0, getHeight() - height, width, getHeight())
        bottomShadow!!.draw(canvas)
    }

    /**
     * Draws items
     *
     * @param canvas the canvas for drawing
     */
    private fun drawItems(canvas: Canvas) {
        canvas.save()

        val top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - height) / 2
        canvas.translate(PADDING.toFloat(), (-top + scrollingOffset).toFloat())

        itemsLayout!!.draw(canvas)

        canvas.restore()
    }

    /**
     * Sets layouts width and height
     *
     * @param width  the layout width
     * @param height the layout height
     */
    private fun layout(width: Int, height: Int) {
        val itemsWidth = width - 2 * PADDING

        itemsLayout!!.layout(0, 0, itemsWidth, height)
    }

    /**
     * Builds view for measuring
     */
    private fun buildViewForMeasuring() {
        // clear all items
        if (itemsLayout != null) {
            recycle.recycleItems(itemsLayout!!, firstItem, ItemsRange())
        } else {
            createItemsLayout()
        }

        // add views
        val addItems = visibleItems / 2
        for (i in currentItem + addItems downTo currentItem - addItems) {
            if (addViewItem(i, true)) {
                firstItem = i
            }
        }
    }

    /**
     * Adds view for item to items layout
     *
     * @param index the item index
     * @param first the flag indicates if view should be first
     * @return true if corresponding item exists and is added
     */
    private fun addViewItem(index: Int, first: Boolean): Boolean {
        val view = getItemView(index)
        if (view != null) {
            if (first) {
                itemsLayout!!.addView(view, 0)
            } else {
                itemsLayout!!.addView(view)
            }

            return true
        }

        return false
    }

    /**
     * Checks whether intem index is valid
     *
     * @param index the item index
     * @return true if item index is not out of bounds or the wheel is cyclic
     */
    private fun isValidItemIndex(index: Int): Boolean {
        return adapter != null && adapter!!.getItemsCount() > 0 &&
                (isCyclic || index >= 0 && index < adapter!!.getItemsCount())
    }

    /**
     * Returns view for specified item
     *
     * @param itemIndex the item index
     * @return item view or empty view if index is out of bounds
     */
    private fun getItemView(itemIndex: Int): View? {
        var index = itemIndex
        if (adapter == null || adapter!!.getItemsCount() == 0) {
            return null
        }
        val count = adapter!!.getItemsCount()
        if (!isValidItemIndex(index)) {
            return adapter!!.getEmptyItem(recycle.emptyItem, itemsLayout)
        } else {
            while (index < 0) {
                index += count
            }
        }

        index %= count
        return adapter!!.getItem(index, recycle.item, itemsLayout)
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    private fun notifyChangingListeners(oldValue: Int, newValue: Int) {
        for (listener in changingListeners) {
            listener.onChanged(this, oldValue, newValue)
        }
    }

    /**
     * Notifies listeners about starting scrolling
     */
    private fun notifyScrollingListenersAboutStart() {
        for (listener in scrollingListeners) {
            listener.onScrollingStarted(this)
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    private fun notifyScrollingListenersAboutEnd() {
        notifyOnSelectedListener(currentItem)
        for (listener in scrollingListeners) {
            listener.onScrollingFinished(this)
        }
    }

    /**
     * Notifies listeners about clicking
     */
    private fun notifyClickListeners(item: Int, isSelected: Boolean) {
        if (adapter != null && adapter!!.onItemClickListener != null) {
            adapter!!.onItemClickListener!!.onItemClicked(this, adapter!!, item, isSelected)
        }
    }

    private fun notifyOnSelectedListener(currentItem: Int) {
        if (adapter != null && adapter!!.onItemSelectedListener != null) {
            adapter!!.onItemSelectedListener!!.onItemSelected(this, adapter!!, currentItem)
        }
    }

    abstract class WheelViewAdapter {
        var wheelView: WheelView? = null
            internal set
        var onItemClickListener: OnItemClickedListener? = null
        var onItemSelectedListener: OnItemSelectedListener? = null

        fun setOnItemClickListener(listener: (WheelView, WheelViewAdapter, Int, Boolean) -> Unit) {
            onItemClickListener = object : OnItemClickedListener {
                override fun onItemClicked(wheel: WheelView, adapter: WheelViewAdapter, index: Int, isSelected: Boolean) {
                    listener.invoke(wheel, adapter, index, isSelected)
                }
            }
        }

        fun setOnItemSelectedListener(listener: (WheelView, WheelViewAdapter, Int) -> Unit) {
            onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(wheel: WheelView, adapter: WheelViewAdapter, index: Int) {
                    listener.invoke(wheel, adapter, index)
                }
            }
        }

        /**
         * Gets items count
         *
         * @return the count of wheel items
         */
        abstract fun getItemsCount(): Int

        val currentIndex: Int
            get() {
                if (wheelView == null) {
                    throw IllegalArgumentException("请先把adapter设置给WheelView")
                }
                return wheelView!!.currentItem
            }

        /**
         * Get a View that displays the data at the specified position in the data set
         *
         * @param index       the item index
         * @param itemView the old view to reuse if possible
         * @param parent      the parent that this view will eventually be attached to
         * @return the wheel item View
         */
        abstract fun getItem(index: Int, itemView: View?, parent: ViewGroup?): View?

        /**
         * Get a View that displays an empty wheel item placed before the first or after
         * the last wheel item.
         *
         * @param emptyView the old view to reuse if possible
         * @param parent      the parent that this view will eventually be attached to
         * @return the empty item View
         */
        abstract fun getEmptyItem(emptyView: View?, parent: ViewGroup?): View?

        /**
         * Register an observer that is called when changes happen to the data used by this adapter.
         *
         * @param observer the observer to be registered
         */
        internal abstract fun registerDataSetObserver(observer: DataSetObserver)

        /**
         * Unregister an observer that has previously been registered
         *
         * @param observer the observer to be unregistered
         */
        internal abstract fun unregisterDataSetObserver(observer: DataSetObserver)

        fun scrollTo(index: Int) {
            scrollTo(index, false, 0)
        }

        @JvmOverloads
        fun scrollTo(index: Int, smooth: Boolean, duration: Int = 500) {
            if (wheelView != null) {
                wheelView!!.setCurrentItem(index, smooth, duration)
            }
        }
    }
}
