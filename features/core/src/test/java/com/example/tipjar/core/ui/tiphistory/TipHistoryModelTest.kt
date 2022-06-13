package com.example.tipjar.core.ui.tiphistory

import com.example.tipjar.core.coroutines.TestDispatcherProvider
import com.example.tipjar.core.ui.helper.CurrencyTextFormatter
import com.example.tipjar.data.currency.ICurrencyRepository
import com.example.tipjar.data.currency.model.CurrencyItem
import com.example.tipjar.data.tiphistory.ITipHistoryRepository
import com.example.tipjar.data.tiphistory.model.TipHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.text.SimpleDateFormat

@RunWith(MockitoJUnitRunner::class)
internal class TipHistoryModelTest {

    @Mock
    lateinit var tipHistoryRepository: ITipHistoryRepository

    @Mock
    lateinit var currencyRepository: ICurrencyRepository

    @Spy
    lateinit var currencyTextFormatter: CurrencyTextFormatter

    @Spy
    lateinit var dispatcherProvider: TestDispatcherProvider

    @InjectMocks
    lateinit var tipHistoryModel: TipHistoryModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcherProvider.main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_getTipHistoryList() = runTest {
        // GIVEN
        val tipHistoryListFromRepository = listOf(createDefaultTipHistoryEntity())
        whenever(tipHistoryRepository.getAllTipHistoryRecords()).thenReturn(tipHistoryListFromRepository)

        // WHEN
        val tipHistoryList = tipHistoryModel.getTipHistoryList()

        // THEN
        assertEquals(tipHistoryListFromRepository, tipHistoryList)
    }

    @Test
    fun test_removeTipHistoryEntity() = runTest {
        // GIVEN
        val entity = createDefaultTipHistoryEntity()

        // WHEN
        tipHistoryModel.removeTipHistoryEntity(entity)

        // THEN
        verify(tipHistoryRepository).removeTipHistoryRecord(entity)
    }

    @Test
    fun test_restoreTipHistoryEntity() = runTest {
        // GIVEN
        val entity = createDefaultTipHistoryEntity()

        // WHEN
        tipHistoryModel.restoreTipHistoryEntity(entity)

        // THEN
        verify(tipHistoryRepository).restoreTipHistoryEntity(entity)
    }

    @Test
    fun test_getReceiptImagePath() = runTest {
        // GIVEN
        val entity = createDefaultTipHistoryEntity()
        whenever(tipHistoryRepository.getReceiptImagePath(entity))
            .thenReturn("mockedPass")

        // WHEN
        val path = tipHistoryModel.getReceiptImagePath(entity)

        // THEN
        assertEquals("mockedPass", path)
    }

    @Test
    fun test_getReceiptImagePath_thumb() = runTest {
        // GIVEN
        val entity = createDefaultTipHistoryEntity()
        whenever(tipHistoryRepository.getReceiptThumbImagePath(entity))
            .thenReturn("mockedPass")

        // WHEN
        val path = tipHistoryModel.getReceiptImagePath(entity, true)

        // THEN
        assertEquals("mockedPass", path)
    }

    @Test
    fun test_getFormattedDateString() = runTest {
        // GIVEN
        val timeStamp = SimpleDateFormat("dd-MM-yyyy")
            .parse("13-06-2022")!!.time

        // WHEN
        val formattedTimestamp = tipHistoryModel.getFormattedDateString(timeStamp)

        // THEN
        assertEquals("2022 June 13", formattedTimestamp)
    }

    @Test
    fun test_getFormattedAmountWithCurrency() = runTest {
        // GIVEN
        val currencyItem = createDefaultCurrencyItem()
        whenever(currencyRepository.getCurrencyItemFromCode(currencyItem.currencyCode))
            .thenReturn(currencyItem)
        val amount = 41.01
        val currencyCode = currencyItem.currencyCode

        // WHEN
        val formattedAmount = tipHistoryModel.getFormattedAmountWithCurrency(
            amount,
            currencyCode
        )

        // THEN
        assertEquals("€41.01", formattedAmount)
    }

    private fun createDefaultCurrencyItem(): CurrencyItem {
        return CurrencyItem(
            currencyCode = "EUR",
            symbol = "€",
            displayName = "Euro",
            defaultFractionDigits = 2
        )
    }

    private fun createDefaultTipHistoryEntity(): TipHistoryEntity {
        return TipHistoryEntity(
            id = 1,
            totalAmount = 40.0,
            tipAmount = 4.0,
            currencyCode = "EUR",
            receiptImageFilename = null,
            timestamp = 1655127864
        )
    }
}