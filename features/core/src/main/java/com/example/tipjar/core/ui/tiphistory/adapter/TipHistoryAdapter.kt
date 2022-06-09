package com.example.tipjar.core.ui.tiphistory.adapter

import android.view.ViewGroup
import com.example.tipjar.shared.ui.base.adapter.BaseRecyclerViewAdapter

typealias OnTipHistoryItemClickListener = (Int) -> Unit

class TipHistoryAdapter(
    private val listener: OnTipHistoryItemClickListener
) : BaseRecyclerViewAdapter<TipHistoryItemVH, TipHistoryListItemUiData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipHistoryItemVH {
        return TipHistoryItemVH(parent, listener)
    }

    override fun onBindViewHolder(holder: TipHistoryItemVH, position: Int) {
        holder.apply(getItem(position))
    }
}