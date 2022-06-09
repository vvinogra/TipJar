package com.example.tipjar.core.ui.tiphistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tipjar.core.R
import com.example.tipjar.core.databinding.FragmentTipHistoryBinding
import com.example.tipjar.core.ui.tiphistory.adapter.TipHistoryAdapter
import com.example.tipjar.core.ui.tiphistory.adapter.TipHistoryListItemUiData
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryNavigation
import com.example.tipjar.shared.ui.base.fragment.BaseFragment
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TipHistoryFragment: BaseFragment(R.layout.fragment_tip_history) {

    private val binding: FragmentTipHistoryBinding by viewBinding()
    private val viewModel: TipHistoryVM by viewModels()

    private lateinit var tipHistoryAdapter: TipHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    launch { tipHistoryUiList.collect(::displayTipHistoryUiList) }
                    launch { navigation.collect(::handleNavigation) }
                }
            }
        }
    }

    private fun setupViews() {
        tipHistoryAdapter = TipHistoryAdapter {  }

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed( ) }

            rvTipHistory.layoutManager = LinearLayoutManager(requireContext())

            rvTipHistory.adapter = tipHistoryAdapter
        }
    }

    private fun displayTipHistoryUiList(list: List<TipHistoryListItemUiData>) {
        tipHistoryAdapter.setData(list)
    }

    private fun handleNavigation(navigation: TipHistoryNavigation?) {
        if (navigation == null) return

        when (navigation) {
            is TipHistoryNavigation.OpenFullSizedTipHistoryItem -> {
                // TODO open tip history details
            }
        }

        viewModel.onNavigationHandled()
    }
}