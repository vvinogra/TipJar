package com.example.tipjar.core.ui.tipsplitter

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tipjar.core.R
import com.example.tipjar.core.databinding.FragmentTipSplitterBinding
import com.example.tipjar.core.navigation.CoreNavigation
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterData
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterNavigation
import com.example.tipjar.core.util.activityresult.OpenCameraContract
import com.example.tipjar.shared.ui.extensions.*
import com.example.tipjar.shared.ui.util.edittext.inputfilter.DecimalDigitsInputFilter
import com.example.tipjar.shared.ui.util.edittext.inputfilter.MinMaxInputFilter
import com.example.tipjar.shared.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TipSplitterFragment : Fragment(R.layout.fragment_tip_splitter) {

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
                MinMaxInputFilter(0, 100)
            )
            etTipPercentage.doOnTextChanged { text, _, _, _ ->
                viewModel.onTipPercentageChanged(text.toString())
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
                    else -> false
                }
            }
        }
    }

    private fun displayUiData(data: TipSplitterData) {
        with(binding) {
            if (data.totalAmount != etEnterAmount.text().toDoubleOrNull()) {
                data.totalAmount?.let {
                    etEnterAmount.setText(it.toString())
                    etTipPercentage.moveCursorToEnd()
                }
            }
            etEnterAmount.hint = data.formatValueWithCurrencyCodeAndFractionalDigits(
                data.totalAmountHintValue,
                false
            )

            etEnterAmount.filters = arrayOf(DecimalDigitsInputFilter(data.fractionalCurrencyDigits))
            etEnterAmount.inputType = if (data.fractionalCurrencyDigits > 0) {
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

            tvTotalTipAmount.text = data.formatValueWithCurrencyCodeAndFractionalDigits(data.totalTip)
            tvTipPerPersonAmount.text = data.formatValueWithCurrencyCodeAndFractionalDigits(data.perPersonTip)

            cbTakePhoto.isChecked = data.shouldTakePhotoOfReceipt

            data.navigationEvent?.let {
                handleNavigation(it)
                viewModel.navigationEventHandled()
            }

            data.toastMessage?.let {
                displayToastMessage(it)
                viewModel.toastMessageDisplayed()
            }
        }
    }

    private fun TipSplitterData.formatValueWithCurrencyCodeAndFractionalDigits(
        value: Double,
        useCurrencySymbol: Boolean = true
    ) : String {
        val template = if (useCurrencySymbol) {
            "${currencySymbol}%.${fractionalCurrencyDigits}f"
        } else {
            "%.${fractionalCurrencyDigits}f"
        }

        return String.format(template, value)
    }

    private fun displayToastMessage(@StringRes messageId: Int) {
        Toast.makeText(requireContext(), messageId, Toast.LENGTH_SHORT).show()
    }

    private fun handleNavigation(navigation: TipSplitterNavigation) {
        when (navigation) {
            TipSplitterNavigation.TakePhotoOfReceipt ->
                startCameraActivityForResult.launchUnit()
            TipSplitterNavigation.TipHistory ->
                coreNavigation.fromTipSplitterToTipHistory(this)
        }
    }
}