package com.example.tipjar.changecurrency.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import com.example.tipjar.changecurrency.R
import com.example.tipjar.changecurrency.databinding.FragmentSelectCurrencyBinding
import com.example.tipjar.shared.ui.base.fragment.BaseFragment
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import com.example.tipjar.changecurrency.ui.adapter.CurrencyAdapter
import com.example.tipjar.changecurrency.ui.model.CurrencyListItemUiData
import com.example.tipjar.changecurrency.ui.model.FilteredCurrencyListData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectCurrencyFragment : BaseFragment(R.layout.fragment_select_currency) {

    private val binding: FragmentSelectCurrencyBinding by viewBinding()
    private val viewModel: SelectCurrencyVM by viewModels()

    private lateinit var currencyAdapter: CurrencyAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = Fade()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    launch { filteredCurrencyListData.collect(::handleFilteredCurrencyListData) }
                }
            }
        }
    }

    private fun setupViews() {
        currencyAdapter = CurrencyAdapter()

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            toolbar.inflateMenu(R.menu.select_currency_menu)

            searchView = toolbar.menu.findItem(R.id.menu_search).actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.onSearchQueryUpdated(newText)
                    return true
                }
            })

            rvCurrencies.layoutManager = LinearLayoutManager(requireContext())
            rvCurrencies.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            rvCurrencies.adapter = currencyAdapter
        }
    }

    private fun handleFilteredCurrencyListData(data: FilteredCurrencyListData) {
        displayTipHistoryUiList(data.currencyList)
        handleScrollToPositionEvent(data.selectedItemPosition)
    }

    private fun handleScrollToPositionEvent(position: Int) {
        position.takeIf { it >= 0 }?.let {
            binding.rvCurrencies.scrollToPosition(it)
        }
    }

    private fun displayTipHistoryUiList(list: List<CurrencyListItemUiData>) {
        currencyAdapter.setData(list)
    }
}