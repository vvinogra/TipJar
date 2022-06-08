package com.example.tipjar.core.ui.tipsplitter

import android.os.Bundle
import android.view.*
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tipjar.core.R
import com.example.tipjar.core.databinding.FragmentTipSplitterBinding
import com.example.tipjar.shared.ui.extensions.alignPrefixCenter
import com.example.tipjar.shared.ui.extensions.alignSuffixCenter
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TipSplitterFragment : Fragment(R.layout.fragment_tip_splitter) {

    private val binding: FragmentTipSplitterBinding by viewBinding()
    private val viewModel: TipSplitterVM by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        with(viewModel) {

        }
    }

    private fun setupViews() {
        with(binding) {
            tlEnterAmount.alignPrefixCenter()
            tlTipPercentage.alignSuffixCenter()

            toolbar.inflateMenu(R.menu.tip_splitter_menu)
            toolbar.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when (it.itemId) {
                    R.id.menu_tip_history -> {
                        viewModel.onTipHistoryClicked()
                        true
                    }
                    else -> false
                }
            }
        }
    }
}