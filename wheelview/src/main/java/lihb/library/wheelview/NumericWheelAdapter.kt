package lihb.library.wheelview

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * Numeric Wheel adapter.
 */
class NumericWheelAdapter : AbstractWheelTextAdapter {
    override fun getItemsCount(): Int {
      return  maxValue - minValue + 1
    }

    // Values
    private var minValue: Int = 0
    private var maxValue: Int = 0

    // format
    private var format: String? = null

    private var label: String = ""

    private var multiple: Int = 0
    
    /**
     * Constructor
     *
     * @param context  the current context
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format   the format string
     */
    @JvmOverloads constructor(context: Context, minValue: Int = DEFAULT_MIN_VALUE, maxValue: Int = DEFAULT_MAX_VALUE, format: String? = null) : super(context) {

        this.minValue = minValue
        this.maxValue = maxValue
        this.format = format
    }

    constructor(context: Context, minValue: Int, maxValue: Int, format: String, multiple: Int) : super(context) {

        this.minValue = minValue
        this.maxValue = maxValue
        this.format = format
        this.multiple = multiple
    }

    public override fun getItemText(index: Int): CharSequence {
        if (index in 0 until getItemsCount()) {
            val value: Int =
                    if (multiple != 0) {
                        minValue + index * multiple
                    } else {
                        minValue + index
                    }
            //            int value = minValue + index;
            return if (format != null) String.format(format!!, value) else value.toString()
        }
        return ""
    }

    override fun getItem(index: Int, itemView: View?, parent: ViewGroup?): View? {
        var convertView = itemView
        if (index in 0 until getItemsCount()) {
            if (convertView == null) {
                convertView = getView(itemResource, parent)
            }
            val textView = getTextView(convertView!!, itemTextResource)
            if (textView != null) {
                val text = getItemText(index)
                textView.text = text.toString() + label
                textView.setPadding(0, 3, 0, 3)
                if (itemResource == AbstractWheelTextAdapter.TEXT_VIEW_ITEM_RESOURCE) {
                    configureTextView(textView)
                }
            }
            return convertView
        }
        return null
    }

    fun setLabel(label: String) {
        this.label = label
    }

    companion object {

        /**
         * The default min value
         */
        const val DEFAULT_MAX_VALUE = 9

        /**
         * The default max value
         */
        private const val DEFAULT_MIN_VALUE = 0
    }

}
