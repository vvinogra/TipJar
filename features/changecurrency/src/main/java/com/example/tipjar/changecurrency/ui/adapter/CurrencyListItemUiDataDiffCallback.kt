package com.example.tipjar.changecurrency.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.tipjar.changecurrency.ui.model.CurrencyListItemUiData

class CurrencyListItemUiDataDiffCallback(
    private val newList: List<CurrencyListItemUiData>,
    private val oldList: List<CurrencyListItemUiData>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}