package lihb.library.wheelview

/**
 * Wheel clicked listener interface.
 *
 * The onItemClicked() method is called whenever a wheel item is clicked
 *  *  New Wheel position is set
 *  *  Wheel view is scrolled
 */
interface OnItemClickedListener {
    /**
     * Callback method to be invoked when current item clicked
     *
     * @param wheel      the wheel view
     * @param index      the index of clicked item
     * @param isSelected 点击的item是否选中
     */
    fun onItemClicked(wheel: WheelView, adapter: WheelView.WheelViewAdapter, index: Int, isSelected: Boolean)
}
