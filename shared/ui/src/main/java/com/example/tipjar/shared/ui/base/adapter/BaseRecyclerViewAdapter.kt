package com.example.tipjar.shared.ui.base.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<VH : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<VH>() {

    private val _dataList = mutableListOf<T>()
    val dataList: List<T> = _dataList

    fun setData(data: List<T>, updateWithAnimation: Boolean = true) {
        setDataInternal(data, updateWithAnimation)
    }

    protected open fun setDataInternal(data: List<T>, updateWithAnimation: Boolean = true) {
        val diffCallback = getDiffUtilCallback(data)
        if (diffCallback == null || !updateWithAnimation) {
            updateDataList(data)
            notifyDataSetChanged()
        } else {
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            diffResult.dispatchUpdatesTo(this)
            updateDataList(data)
        }
    }

    private fun updateDataList(data: List<T>) {
        _dataList.clear()
        _dataList.addAll(data)
    }

    override fun getItemCount() = _dataList.size

    fun getItem(position: Int) = _dataList[position]

    open fun getDiffUtilCallback(newList: List<T>): DiffUtil.Callback? = null

    open fun getSpanCount(position: Int): Int = 1

}
