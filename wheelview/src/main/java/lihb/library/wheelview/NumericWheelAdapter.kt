/*
 *  Copyright 2011 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package lihb.library.wheelview

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * Numeric Wheel adapter.
 */
class NumericWheelAdapter : AbstractWheelTextAdapter {

    // Values
    private var minValue: Int = 0
    private var maxValue: Int = 0

    // format
    private var format: String? = null

    private var label: String? = null

    private var multiple: Int = 0

    override val itemsCount: Int
        get() = maxValue - minValue + 1

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
        if (index in 0..(itemsCount - 1)) {

            val value: Int =
                    if (multiple != 0) {
                        minValue + index * multiple
                    } else {
                        minValue + index
                    }
            //            int value = minValue + index;
            return if (format != null) String.format(format!!, value) else Integer.toString(value)
        }
        return ""
    }

    override fun getItem(index: Int, itemView: View?, parent: ViewGroup?): View? {
        var convertView = itemView
        if (index in 0..(itemsCount - 1)) {
            if (convertView == null) {
                convertView = getView(itemResource, parent)
            }
            val textView = getTextView(convertView!!, itemTextResource)
            if (textView != null) {
                var text: CharSequence? = getItemText(index)
                if (text == null) {
                    text = ""
                }
                textView.text = text.toString() + label!!
                textView.setPadding(0, 3, 0, 3)
                if (itemResource == AbstractWheelTextAdapter.Companion.TEXT_VIEW_ITEM_RESOURCE) {
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
