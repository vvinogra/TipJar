package com.example.tipjar.core.ui.tipsplitter

import android.net.Uri
import com.example.tipjar.core.coroutines.TestDispatcherProvider
import com.example.tipjar.core.ui.helper.CurrencyTextFormatter
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterData
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterFormattedDoubleValue
import com.example.tipjar.core.ui.tipsplitter.model.TipSplitterUserInputData
import com.example.tipjar.data.currency.ICurrencyRepository
import com.example.tipjar.data.currency.model.CurrencyItem
import com.example.tipjar.data.phonefeature.IUserPhoneFeatureManager
import com.example.tipjar.data.tiphistory.ITipHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
internal class TipSplitterModelTest {

    @Mock
    lateinit var userPhoneFeatureManager: IUserPhoneFeatureManager

    @Mock
    lateinit var tipHistoryRepository: ITipHistoryRepository

    @Mock
    lateinit var currencyRepository: ICurrencyRepository

    @Spy
    lateinit var currencyTextFormatter: CurrencyTextFormatter

    @Spy
    lateinit var dispatcherProvider: TestDispatcherProvider

    @InjectMocks
    lateinit var tipSplitterModel: TipSplitterModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcherProvider.main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_canTakePhotoOfReceipt() {
        // GIVEN
        whenever(userPhoneFeatureManager.isCameraAvailable()).thenReturn(true)

        // WHEN
        val canTakePhotoOfReceipt = tipSplitterModel.canTakePhotoOfReceipt()

        // THEN
        assertTrue(canTakePhotoOfReceipt)
    }

    @Test
    fun test_createUriToSaveOriginalImage() {
        // GIVEN
        val receiptImageUri = Uri.EMPTY
        whenever(tipHistoryRepository.createUriToSaveReceiptImage()).thenReturn(receiptImageUri)

        // WHEN
        val uri = tipSplitterModel.createUriToSaveOriginalImage()

        // THEN
        assertEquals(receiptImageUri, uri)
    }

    @Test
    fun test_saveTipInHistory() = runTest {
        // GIVEN
        val totalAmount = 42.0
        val receiptImageUri = Uri.EMPTY
        val tipSplitterData = getDefaultTipSplitterData().run {
            copy(
                totalAmount = this.totalAmount.copy(
                    value = totalAmount
                )
            )
        }

        // WHEN
        tipSplitterModel.saveTipInHistory(tipSplitterData, receiptImageUri)

        // THEN
        verify(tipHistoryRepository).createTipHistoryRecord(
            totalAmount,
            tipSplitterData.totalTip.originalValue,
            tipSplitterData.selectedCurrency.currencyCode,
            receiptImageUri
        )
    }

    @Test
    fun test_getUpdatedDataAfterCurrencyChange_fractionDigitsDecreased() {
        // GIVEN
        val previouslySelectedCurrency = getDefaultCurrencyItem().copy(
            symbol = "$",
            defaultFractionDigits = 2
        )
        val newCurrencyItem = previouslySelectedCurrency.copy(
            defaultFractionDigits = 0
        )
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                totalAmount = TipSplitterUserInputData("100.36", 100.36),
                totalAmountHintValue = TipSplitterFormattedDoubleValue(20.4, "20.41"),
                totalTip = TipSplitterFormattedDoubleValue(2.4, "$2.04"),
                perPersonTip = TipSplitterFormattedDoubleValue(2.4, "$2.04"),
                selectedCurrency = previouslySelectedCurrency
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedDataAfterCurrencyChange(tipSplitterData, newCurrencyItem)

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(totalAmount, TipSplitterUserInputData("100", 100.0))
            assertEquals(totalAmountHintValue, TipSplitterFormattedDoubleValue(20.4, "20"))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(2.4, "$2"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(2.4, "$2"))
            assertEquals(selectedCurrency, newCurrencyItem)
        }
    }

    @Test
    fun test_getUpdatedDataAfterCurrencyChange_fractionDigitsIncreased() {
        // GIVEN
        val previouslySelectedCurrency = getDefaultCurrencyItem().copy(
            symbol = "$",
            defaultFractionDigits = 0
        )
        val newCurrencyItem = previouslySelectedCurrency.copy(
            defaultFractionDigits = 1
        )
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                totalAmount = TipSplitterUserInputData("100", 100.0),
                totalAmountHintValue = TipSplitterFormattedDoubleValue(20.0, "20"),
                totalTip = TipSplitterFormattedDoubleValue(2.0, "$2"),
                perPersonTip = TipSplitterFormattedDoubleValue(2.0, "$2"),
                selectedCurrency = previouslySelectedCurrency
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedDataAfterCurrencyChange(tipSplitterData, newCurrencyItem)

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(totalAmount, TipSplitterUserInputData("100", 100.0))
            assertEquals(totalAmountHintValue, TipSplitterFormattedDoubleValue(20.0, "20.0"))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(2.0, "$2.0"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(2.0, "$2.0"))
            assertEquals(selectedCurrency, newCurrencyItem)
        }
    }

    @Test
    fun test_getUpdatedDataAfterCurrencyChange_fractionDigitsUnchanged() {
        // GIVEN
        val previouslySelectedCurrency = getDefaultCurrencyItem().copy(
            symbol = "€",
            defaultFractionDigits = 2
        )
        val newCurrencyItem = previouslySelectedCurrency.copy(
            defaultFractionDigits = 2
        )
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                totalAmount = TipSplitterUserInputData("100.43", 100.43),
                totalAmountHintValue = TipSplitterFormattedDoubleValue(20.79, "20.79"),
                totalTip = TipSplitterFormattedDoubleValue(2.07, "€2.07"),
                perPersonTip = TipSplitterFormattedDoubleValue(1.03, "€1.03"),
                selectedCurrency = previouslySelectedCurrency
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedDataAfterCurrencyChange(tipSplitterData, newCurrencyItem)

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(totalAmount, TipSplitterUserInputData("100.43", 100.43))
            assertEquals(totalAmountHintValue, TipSplitterFormattedDoubleValue(20.79, "20.79"))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(2.07, "€2.07"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(1.03, "€1.03"))
            assertEquals(selectedCurrency, newCurrencyItem)
        }
    }

    @Test
    fun test_getUpdatedTipSplitterAfterCalculation_totalAmount() {
        // GIVEN
        val newTotalAmount = TipSplitterUserInputData<Double?>("200.4", 200.4)
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                tipPercentage = 10,
                peopleCount = 1,
                totalAmount = TipSplitterUserInputData("100.00", 100.00),
                totalAmountHintValue = TipSplitterFormattedDoubleValue(20.79, "20.79"),
                totalTip = TipSplitterFormattedDoubleValue(10.00, "10.00"),
                perPersonTip = TipSplitterFormattedDoubleValue(10.00, "10.00")
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedTipSplitterAfterCalculation(
                data = tipSplitterData,
                totalAmount = newTotalAmount
            )

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(tipPercentage, 10)
            assertEquals(peopleCount, 1)
            assertEquals(totalAmount, TipSplitterUserInputData("200.4", 200.4))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(20.04, "$20.04"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(20.04, "$20.04"))
        }
    }

    @Test
    fun test_getUpdatedTipSplitterAfterCalculation_totalAmount_empty() {
        // GIVEN
        val newTotalAmount = TipSplitterUserInputData<Double?>("", null)
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                tipPercentage = 10,
                peopleCount = 4,
                totalAmount = TipSplitterUserInputData("100.00", 100.00),
                totalAmountHintValue = TipSplitterFormattedDoubleValue(40.00, "40.00"),
                totalTip = TipSplitterFormattedDoubleValue(10.00, "10.00"),
                perPersonTip = TipSplitterFormattedDoubleValue(10.00, "10.00")
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedTipSplitterAfterCalculation(
                data = tipSplitterData,
                totalAmount = newTotalAmount
            )

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(tipPercentage, 10)
            assertEquals(peopleCount, 4)
            assertEquals(totalAmount, TipSplitterUserInputData("", null))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(4.0, "$4.00"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(1.0, "$1.00"))
        }
    }

    @Test
    fun test_getUpdatedTipSplitterAfterCalculation_peopleCount() {
        // GIVEN
        val newPeopleCount = 2
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                tipPercentage = 10,
                peopleCount = 1,
                totalAmount = TipSplitterUserInputData("100.00", 100.00),
                totalAmountHintValue = TipSplitterFormattedDoubleValue(20.79, "20.79"),
                totalTip = TipSplitterFormattedDoubleValue(10.00, "10.00"),
                perPersonTip = TipSplitterFormattedDoubleValue(10.00, "10.00")
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedTipSplitterAfterCalculation(
                data = tipSplitterData,
                peopleCount = newPeopleCount
            )

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(tipPercentage, 10)
            assertEquals(peopleCount, 2)
            assertEquals(totalAmount, TipSplitterUserInputData("100.00", 100.0))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(10.00, "$10.00"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(5.0, "$5.00"))
        }
    }

    @Test
    fun test_getUpdatedTipSplitterAfterCalculation_tipPercentage() {
        // GIVEN
        val newTipPercentage = 4
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                tipPercentage = 10,
                peopleCount = 2,
                totalAmount = TipSplitterUserInputData("100.00", 100.00),
                totalAmountHintValue = TipSplitterFormattedDoubleValue(20.79, "20.79"),
                totalTip = TipSplitterFormattedDoubleValue(10.00, "10.00"),
                perPersonTip = TipSplitterFormattedDoubleValue(10.00, "10.00")
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedTipSplitterAfterCalculation(
                data = tipSplitterData,
                tipPercentage = newTipPercentage
            )

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(tipPercentage, 4)
            assertEquals(peopleCount, 2)
            assertEquals(totalAmount, TipSplitterUserInputData("100.00", 100.0))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(4.0, "$4.00"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(2.0, "$2.00"))
        }
    }

    @Test
    fun test_getUpdatedTipSplitterAfterCalculation_tipPercentage_empty() {
        // GIVEN
        val newTipPercentage = null
        val tipSplitterData = getDefaultTipSplitterData()
            .copy(
                tipPercentage = 10,
                peopleCount = 2,
                totalAmount = TipSplitterUserInputData("100.00", 100.00),
                tipPercentageHintValue = 40,
                totalAmountHintValue = TipSplitterFormattedDoubleValue(20.79, "20.79"),
                totalTip = TipSplitterFormattedDoubleValue(10.00, "10.00"),
                perPersonTip = TipSplitterFormattedDoubleValue(10.00, "10.00")
            )

        // WHEN
        val updatedTipSplitterData =
            tipSplitterModel.getUpdatedTipSplitterAfterCalculation(
                data = tipSplitterData,
                tipPercentage = newTipPercentage,
                setForceNullableTipPercentage = true
            )

        // THEN
        with(updatedTipSplitterData) {
            assertEquals(tipPercentage, null)
            assertEquals(peopleCount, 2)
            assertEquals(totalAmount, TipSplitterUserInputData("100.00", 100.0))
            assertEquals(totalTip, TipSplitterFormattedDoubleValue(40.0, "$40.00"))
            assertEquals(perPersonTip, TipSplitterFormattedDoubleValue(20.0, "$20.00"))
        }
    }

    private fun getDefaultTipSplitterData(): TipSplitterData {
        return TipSplitterData(
            tipPercentage = 10,
            tipPercentageHintValue = 15,
            peopleCount = 1,
            totalAmount = TipSplitterUserInputData("40.00", 40.0),
            totalAmountHintValue = TipSplitterFormattedDoubleValue(0.0, "0.0"),
            totalTip = TipSplitterFormattedDoubleValue(0.0, "0.0"),
            perPersonTip = TipSplitterFormattedDoubleValue(0.0, "0.0"),
            shouldTakePhotoOfReceipt = false,
            selectedCurrency = getDefaultCurrencyItem(),
            showCantOpenCameraToast = null,
            navigationEvent = null
        )
    }

    private fun getDefaultCurrencyItem(): CurrencyItem {
        return CurrencyItem(
            currencyCode = "USD",
            symbol = "$",
            displayName = "US Dollar",
            defaultFractionDigits = 2
        )
    }
}