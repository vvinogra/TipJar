package com.example.tipjar.changecurrency.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tipjar.changecurrency.R
import com.example.tipjar.changecurrency.databinding.VhCurrencyItemBinding
import com.example.tipjar.shared.ui.extensions.inflate
import com.example.tipjar.changecurrency.ui.model.CurrencyListItemUiData

class CurrencyItemVH(
    parent: ViewGroup
) : RecyclerView.ViewHolder(parent.inflate(R.layout.vh_currency_item)) {

    private val binding = VhCurrencyItemBinding.bind(itemView)

    fun apply(data: CurrencyListItemUiData) {
        with(binding) {
            flRoot.setOnClickListener {
                data.clickListener()
            }

            rbCurrency.text = binding.root.context.getString(
                R.string.formatted_currency,
                data.name,
                data.symbol
            )

            setSelected(data.isSelected)
        }
    }

    fun setSelected(isSelected: Boolean) {
        binding.rbCurrency.isChecked = isSelected
    }
}