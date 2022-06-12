package com.github.vvinogra.changecurrency.ui.adapter

import android.view.ViewGroup
import com.example.tipjar.shared.ui.base.adapter.PreserveScrollPositionRecyclerViewAdapter
import com.github.vvinogra.changecurrency.ui.model.CurrencyListItemUiData

class CurrencyAdapter : PreserveScrollPositionRecyclerViewAdapter<CurrencyItemVH, CurrencyListItemUiData>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemVH {
        return CurrencyItemVH(parent)
    }

    override fun onBindViewHolder(holder: CurrencyItemVH, position: Int) {
        holder.apply(getItem(position))
    }

    override fun getDiffUtilCallback(newList: List<CurrencyListItemUiData>) =
        CurrencyListItemUiDataDiffCallback(newList, dataList)
}