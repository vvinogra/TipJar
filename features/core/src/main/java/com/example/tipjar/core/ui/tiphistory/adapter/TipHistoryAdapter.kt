package com.example.tipjar.core.ui.tiphistory.adapter

import android.view.ViewGroup
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryListItemUiData
import com.example.tipjar.shared.ui.base.adapter.PreserveScrollPositionRecyclerViewAdapter

class TipHistoryAdapter : PreserveScrollPositionRecyclerViewAdapter<TipHistoryItemVH, TipHistoryListItemUiData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipHistoryItemVH {
        return TipHistoryItemVH(parent)
    }

    override fun onBindViewHolder(holder: TipHistoryItemVH, position: Int) {
        holder.apply(getItem(position))
    }

    override fun getDiffUtilCallback(newList: List<TipHistoryListItemUiData>) =
        TipHistoryListItemUiDataDiffCallback(newList, dataList)
}