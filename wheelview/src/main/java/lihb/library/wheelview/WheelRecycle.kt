
package lihb.library.wheelview

import android.view.View
import android.widget.LinearLayout
import java.util.*

/**
 * Recycle stores wheel items to reuse.
 */
class WheelRecycle
/**
 * Constructor
 * @param wheel the wheel view
 */
(// Wheel view
        private val wheel: WheelView) {
    // Cached items
    private var items: MutableList<View>? = null

    // Cached empty items
    private var emptyItems: MutableList<View>? = null

    /**
     * Gets item view
     * @return the cached view
     */
    val item: View?
        get() = getCachedView(items)

    /**
     * Gets empty item view
     * @return the cached empty view
     */
    val emptyItem: View?
        get() = getCachedView(emptyItems)

    /**
     * Recycles items from specified layout.
     * There are saved only items not included to specified range.
     * All the cached items are removed from original layout.
     *
     * @param layout the layout containing items to be cached
     * @param firstItem the number of first item in layout
     * @param range the range of current wheel items
     * @return the new value of first item number
     */
    fun recycleItems(layout: LinearLayout, firstItem: Int, range: IntRange): Int {
        var firstItemVar = firstItem
        var index = firstItemVar
        var i = 0
        while (i < layout.childCount) {
            if (!range.contains(index)) {
                recycleView(layout.getChildAt(i), index)
                layout.removeViewAt(i)
                if (i == 0) { // first item
                    firstItemVar++
                }
            } else {
                i++ // go to next item
            }
            index++
        }
        return firstItemVar
    }

    /**
     * Clears all views
     */
    fun clearAll() {
        if (items != null) {
            items!!.clear()
        }
        if (emptyItems != null) {
            emptyItems!!.clear()
        }
    }

    /**
     * Adds view to specified cache. Creates a cache list if it is null.
     * @param view the view to be cached
     * @param cache the cache list
     * @return the cache list
     */
    private fun addView(view: View, cache: MutableList<View>?): MutableList<View> {
        var cacheVar = cache
        if (cacheVar == null) {
            cacheVar = LinkedList()
        }

        cacheVar.add(view)
        return cacheVar
    }

    /**
     * Adds view to cache. Determines view type (item view or empty one) by index.
     * @param view the view to be cached
     * @param index the index of view
     */
    private fun recycleView(view: View, index: Int) {
        var indexVar = index
        val count = wheel.adapter!!.getItemsCount()

        if ((indexVar < 0 || indexVar >= count) && !wheel.isCyclic()) {
            // empty view
            emptyItems = addView(view, emptyItems)
        } else {
            while (indexVar < 0) {
                indexVar += count
            }
            indexVar %= count
            items = addView(view, items)
        }
    }

    /**
     * Gets view from specified cache.
     * @param cache the cache
     * @return the first view from cache.
     */
    private fun getCachedView(cache: MutableList<View>?): View? {
        if (cache != null && cache.size > 0) {
            val view = cache[0]
            cache.removeAt(0)
            return view
        }
        return null
    }

}
