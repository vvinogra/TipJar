package com.example.tipjar.shared.ui.base.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * According to this answer - https://stackoverflow.com/a/44053550/11540133.
 * We can keep the scroll position by calling [RecyclerView.LayoutManager.onSaveInstanceState]
 * and [RecyclerView.LayoutManager.onRestoreInstanceState] functions.
 */
abstract class PreserveScrollPositionRecyclerViewAdapter<VH : RecyclerView.ViewHolder, T>
    : BaseRecyclerViewAdapter<VH, T>() {

    private var recyclerView: RecyclerView? = null

    private val layoutManager: RecyclerView.LayoutManager?
        get() = recyclerView?.layoutManager

    override fun setDataInternal(data: List<T>, updateWithAnimation: Boolean) {
        val recyclerViewState = layoutManager?.onSaveInstanceState()

        super.setDataInternal(data, updateWithAnimation)

        layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        this.recyclerView = null
    }
}