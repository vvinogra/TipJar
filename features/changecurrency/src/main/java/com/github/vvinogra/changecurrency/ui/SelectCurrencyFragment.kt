package com.github.vvinogra.changecurrency.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tipjar.shared.ui.base.fragment.BaseFragment
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import com.github.vvinogra.changecurrency.R
import com.github.vvinogra.changecurrency.databinding.FragmentSelectCurrencyBinding
import com.github.vvinogra.changecurrency.ui.adapter.CurrencyAdapter
import com.github.vvinogra.changecurrency.ui.model.CurrencyListItemUiData
import com.github.vvinogra.changecurrency.ui.model.SelectCurrencyData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectCurrencyFragment : BaseFragment(R.layout.fragment_select_currency) {

    private val binding: FragmentSelectCurrencyBinding by viewBinding()
    private val viewModel: SelectCurrencyVM by viewModels()

    private lateinit var currencyAdapter: CurrencyAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    launch { selectCurrencyUiData.collect(::handleSelectCurrencyUiData) }
                }
            }
        }
    }

    private fun setupViews() {
        currencyAdapter = CurrencyAdapter()

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }

            rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
            rvCurrencies.addItemDecoration(DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            ))
            rvCurrencies.adapter = currencyAdapter
        }
    }

    private fun handleSelectCurrencyUiData(data: SelectCurrencyData) {
        displayTipHistoryUiList(data.currencyList)
    }

    private fun displayTipHistoryUiList(list: List<CurrencyListItemUiData>) {
        currencyAdapter.setData(list)
    }
}