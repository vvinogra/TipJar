package com.example.tipjar.core.ui.tiphistory.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tipjar.core.R
import com.example.tipjar.core.databinding.VhTipHistoryItemBinding
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryListItemUiData
import com.example.tipjar.shared.ui.extensions.inflate

class TipHistoryItemVH(
    parent: ViewGroup
) : RecyclerView.ViewHolder(parent.inflate(R.layout.vh_tip_history_item)) {

    private val binding = VhTipHistoryItemBinding.bind(itemView)

    fun apply(data: TipHistoryListItemUiData) {
        with(binding) {
            tvDate.text = data.date
            tvTipAmount.text = root.context.getString(R.string.tip_amount_placeholder, data.totalTipAmount)
            tvTotalAmount.text = data.totalAmount

            data.imagePath?.let {
                Glide.with(root.context)
                    .load(data.imagePath)
                    .into(ivTipImage)
            } ?: run {
                Glide.with(root.context)
                    .clear(ivTipImage)
            }

            ivTipImage.setOnClickListener {
                data.onTipHistoryItemImageClick()
            }
        }
    }
}