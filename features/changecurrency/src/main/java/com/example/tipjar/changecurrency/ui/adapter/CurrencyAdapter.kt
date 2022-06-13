package com.example.tipjar.changecurrency.ui.adapter

import android.view.ViewGroup
import com.example.tipjar.shared.ui.base.adapter.BaseRecyclerViewAdapter
import com.example.tipjar.changecurrency.ui.model.CurrencyListItemUiData

class CurrencyAdapter : BaseRecyclerViewAdapter<CurrencyItemVH, CurrencyListItemUiData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemVH {
        return CurrencyItemVH(parent)
    }

    override fun onBindViewHolder(holder: CurrencyItemVH, position: Int) {
        holder.apply(getItem(position))
    }

    override fun onBindViewHolder(
        holder: CurrencyItemVH,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when {
            payloads.contains(CURRENCY_SELECTED_PAYLOAD) -> holder.setSelected(getItem(position).isSelected)
            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getDiffUtilCallback(newList: List<CurrencyListItemUiData>) =
        CurrencyListItemUiDataDiffCallback(newList, dataList)
}