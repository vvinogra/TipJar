package com.example.tipjar.core.ui.tipdetails

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.tipjar.core.R
import com.example.tipjar.core.databinding.FragmentTipDetailsBinding
import com.example.tipjar.core.ui.tipdetails.navigation.TipDetailsNavValues
import com.example.tipjar.shared.ui.base.fragment.BaseDialogFragment
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TipDetailsFragment : BaseDialogFragment(R.layout.fragment_tip_details) {

    private val binding: FragmentTipDetailsBinding by viewBinding()
    private val viewModel: TipDetailsVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    launch { tipDetailsUiData.collect(::displayTipDetailsUiData) }
                }
            }
        }
    }

    private fun setupViews() {
        // Making transparent background
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // Stretching dialog to the full width
        dialog?.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        with(binding) {
            root.setOnClickListener { onBackPressed() }
        }
    }

    private fun displayTipDetailsUiData(data: TipDetailsNavValues) {
        with(binding) {
            tvDate.text = data.date
            tvTipAmount.text = getString(R.string.tip_amount_placeholder, data.tipTotalAmount)
            tvTotalAmount.text = data.totalAmount

            Glide.with(requireContext())
                .load(data.imagePath)
                .into(ivTipImage)
        }
    }
}