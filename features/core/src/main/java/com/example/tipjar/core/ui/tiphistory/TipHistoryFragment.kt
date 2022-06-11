package com.example.tipjar.core.ui.tiphistory

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tipjar.core.R
import com.example.tipjar.core.databinding.FragmentTipHistoryBinding
import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.core.ui.tiphistory.adapter.TipHistoryAdapter
import com.example.tipjar.core.ui.tiphistory.adapter.TipHistoryItemVH
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryListItemUiData
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryNavigation
import com.example.tipjar.core.ui.tiphistory.model.TipHistoryUiData
import com.example.tipjar.shared.ui.base.fragment.BaseFragment
import com.example.tipjar.shared.ui.util.recyclerview.SwipeItemTouchHelperCallback
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TipHistoryFragment: BaseFragment(R.layout.fragment_tip_history) {

    @Inject
    lateinit var coreNavigation: CoreNavigation

    private val binding: FragmentTipHistoryBinding by viewBinding()
    private val viewModel: TipHistoryVM by viewModels()

    private lateinit var tipHistoryAdapter: TipHistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    launch { uiData.collect(::handleUiData) }
                }
            }
        }
    }

    private fun setupViews() {
        tipHistoryAdapter = TipHistoryAdapter()

        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed( ) }

            rvTipHistory.layoutManager = LinearLayoutManager(requireContext())
            rvTipHistory.adapter = tipHistoryAdapter

            val itemTouchHelperCallback = SwipeItemTouchHelperCallback(
                context = rvTipHistory.context,
                iconRes = R.drawable.ic_baseline_delete_24,
                backgroundColor = Color.RED,
                removeItemCallback = ::onRemoveTipHistoryItemBySlide
            )
            ItemTouchHelper(itemTouchHelperCallback).apply {
                attachToRecyclerView(rvTipHistory)
            }
        }
    }

    private fun onRemoveTipHistoryItemBySlide(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.bindingAdapterPosition
        val bitmap = (viewHolder as TipHistoryItemVH).getCurrentTipImageIfSet()

        viewModel.removeTipHistoryItem(tipHistoryAdapter.getItem(position), position, bitmap)
    }

    private fun undoDeleteRemovedTipHistoryItem() {
        viewModel.undoRecentItemDeletion()
    }

    private fun displayTipHistoryUiList(list: List<TipHistoryListItemUiData>) {
        tipHistoryAdapter.setData(list)
    }

    private fun handleUiData(uiData: TipHistoryUiData) {
        displayTipHistoryUiList(uiData.historyList)

        displayUndoDeleteSnackbar(uiData.showUndoDeleteSnackbarEvent)
        handleNavigation(uiData.navigation)
    }

    private fun displayUndoDeleteSnackbar(undoDeleteSnackbarEvent: Unit?) {
        if (undoDeleteSnackbarEvent == null) return

        val snackbar = Snackbar.make(
            binding.root,
            R.string.one_item_deleted,
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction(R.string.snack_bar_undo) {
            undoDeleteRemovedTipHistoryItem()
        }
        snackbar.show()

        viewModel.onUndoSnackbarIsShown()
    }

    private fun handleNavigation(navigation: TipHistoryNavigation?) {
        if (navigation == null) return

        when (navigation) {
            is TipHistoryNavigation.OpenFullSizedTipHistoryItem ->
                coreNavigation.fromTipHistoryToTipDetails(
                    this,
                    navigation.tipDetailsNavValues
                )
        }

        viewModel.onNavigationHandled()
    }
}