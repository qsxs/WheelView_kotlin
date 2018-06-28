package lihb.library.wheelview

import android.view.View
import android.view.ViewGroup

/**
 * Abstract Wheel adapter.
 */
abstract class AbstractWheelAdapter : WheelView.WheelViewAdapter() {

    override fun getEmptyItem(emptyView: View?, parent: ViewGroup?): View? {
        return null
    }
}
