package lihb.library.wheelview

import android.content.Context

/**
 * The simple Array wheel adapter
 * @param <T> the element type
</T> */
class ArrayWheelAdapter<T>(context: Context, private val items: Array<T>) : AbstractWheelTextAdapter(context) {
    override fun getItemsCount(): Int {
        return items.size
    }

    public override fun getItemText(index: Int): CharSequence {
        if (index >= 0 && index < items.size) {
            val item = items[index]
            return if (item is CharSequence) {
                item
            } else item.toString()
        }
        return ""
    }
}
