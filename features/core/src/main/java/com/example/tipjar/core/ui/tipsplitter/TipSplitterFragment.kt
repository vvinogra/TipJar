package com.example.tipjar.core.ui.tipsplitter

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tipjar.core.R
import com.example.tipjar.core.databinding.FragmentTipSplitterBinding
import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterData
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterNavigation
import com.example.tipjar.shared.ui.util.activityresult.OpenCameraContract
import com.example.tipjar.shared.ui.base.fragment.BaseFragment
import com.example.tipjar.shared.ui.extensions.*
import com.example.tipjar.shared.ui.util.edittext.inputfilter.DecimalDigitsInputFilter
import com.example.tipjar.shared.ui.util.edittext.inputfilter.MinMaxInputFilter
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TipSplitterFragment : BaseFragment(R.layout.fragment_tip_splitter) {

    @Inject
    lateinit var coreNavigation: CoreNavigation

    private val binding: FragmentTipSplitterBinding by viewBinding()
    private val viewModel: TipSplitterVM by viewModels()

    private val startCameraActivityForResult = registerForActivityResult(OpenCameraContract()) {
        viewModel.onUserTookReceiptPhoto(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    launch {
                        uiData.collect(::displayUiData)
                    }
                }
            }
        }
    }

    private fun setupViews() {
        with(binding) {
            tlEnterAmount.alignPrefixCenter()
            tlTipPercentage.alignSuffixCenter()

            etEnterAmount.doOnTextChanged { text, _, _, _ ->
                viewModel.onTotalAmountChanged(text.toString())
            }

            mcvAddPeople.setOnClickListener { viewModel.onPlusButtonClicked() }
            mcvMinusPeople.setOnClickListener { viewModel.onMinusButtonClicked() }

            etTipPercentage.filters = arrayOf(
                MinMaxInputFilter(MIN_PERCENTAGE_VALUE, MAX_PERCENTAGE_VALUE)
            )
            etTipPercentage.doOnTextChanged { text, _, _, _ ->
                viewModel.onTipPercentageChanged(text.toString())
            }
            etTipPercentage.doOnActionDone {
                etTipPercentage.clearFocus()
            }

            cbTakePhoto.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onTakePhotoOfReceiptCheckedChanged(isChecked)
            }

            btnSavePayment.setOnClickListener {
                viewModel.onSaveButtonClicked()
            }

            toolbar.inflateMenu(R.menu.tip_splitter_menu)
            toolbar.setOnMenuItemClickListener {
                return@setOnMenuItemClickListener when (it.itemId) {
                    R.id.menu_tip_history -> {
                        viewModel.onTipHistoryClicked()
                        true
                    }
                    R.id.menu_change_currency -> {
                        viewModel.onChangeCurrencyClicked()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun displayUiData(data: TipSplitterData) {
        with(binding) {
            val totalAmountUserInput = data.totalAmount.userInput
            if (totalAmountUserInput != etEnterAmount.text() && totalAmountUserInput.isNotEmpty()) {
                etEnterAmount.setText(totalAmountUserInput)
                etEnterAmount.moveCursorToEnd()
            }

            etEnterAmount.hint = data.totalAmountHintValue.formattedValue
            tlEnterAmount.prefixText = data.selectedCurrency.symbol

            etEnterAmount.filters = arrayOf(
                DecimalDigitsInputFilter(data.selectedCurrency.defaultFractionDigits)
            )
            etEnterAmount.inputType = if (data.selectedCurrency.defaultFractionDigits > 0) {
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            } else {
                InputType.TYPE_CLASS_NUMBER
            }

            tvPeopleAmount.text = data.peopleCount.toString()

            if (data.tipPercentage != etTipPercentage.text().toIntOrNull()) {
                data.tipPercentage?.let {
                    etTipPercentage.setText(it.toString())
                    etTipPercentage.moveCursorToEnd()
                }
            }
            etTipPercentage.hint = data.tipPercentageHintValue.toString()

            tvTotalTipAmount.text = data.totalTip.formattedValue
            tvTipPerPersonAmount.text = data.perPersonTip.formattedValue

            cbTakePhoto.isChecked = data.shouldTakePhotoOfReceipt

            data.navigationEvent?.let {
                handleNavigation(it)
                viewModel.navigationEventHandled()
            }

            data.showCantOpenCameraToast?.let {
                displayToastMessage(R.string.can_not_take_photo_of_receipt_toast)
                viewModel.cantOpenCameraToastMessageDisplayed()
            }
        }
    }

    private fun displayToastMessage(@StringRes messageId: Int) {
        Toast.makeText(requireContext(), messageId, Toast.LENGTH_SHORT).show()
    }

    private fun handleNavigation(navigation: TipSplitterNavigation) {
        when (navigation) {
            is TipSplitterNavigation.TakePhotoOfReceipt ->
                startCameraActivityForResult.safeLaunch(navigation.uri) {
                    viewModel.onFailedToOpenExternalCameraApp()
                }
            TipSplitterNavigation.TipHistory ->
                coreNavigation.fromTipSplitterToTipHistory(this)
            TipSplitterNavigation.ChangeCurrency ->
                coreNavigation.fromTipSplitterToSelectCurrency(this)
        }
    }
}