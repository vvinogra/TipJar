package com.example.tipjar.core.ui.tiphistory.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryListItemUiData

class TipHistoryListItemUiDataDiffCallback(
    private val newList: List<TipHistoryListItemUiData>,
    private val oldList: List<TipHistoryListItemUiData>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}