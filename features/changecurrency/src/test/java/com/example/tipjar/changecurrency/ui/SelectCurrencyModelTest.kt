package com.example.tipjar.changecurrency.ui

import com.example.tipjar.changecurrency.coroutines.TestDispatcherProvider
import com.example.tipjar.changecurrency.ui.model.CurrencyListItemUiData
import com.example.tipjar.changecurrency.ui.model.SelectCurrencyData
import com.example.tipjar.data.currency.ICurrencyRepository
import com.example.tipjar.data.currency.model.CurrencyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
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

@RunWith(MockitoJUnitRunner::class)
internal class SelectCurrencyModelTest {

    @Mock
    lateinit var currencyRepository: ICurrencyRepository

    @Spy
    lateinit var dispatcherProvider: TestDispatcherProvider

    @InjectMocks
    lateinit var selectCurrencyModel: SelectCurrencyModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcherProvider.main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun test_getFilteredCurrencyListDataTransformedFlow_searchByName() = runTest {
        // GIVEN
        val searchResultCurrency = createCurrencyListItemUiData().copy(
            name = "US dollar",
            code = "USD"
        )
        val data = createSelectCurrencyData()
            .copy(
                currencyList = listOf(
                    createCurrencyListItemUiData().copy(
                        name = "Ukrainian Hryvnia",
                        code = "UAH"
                    ),
                    createCurrencyListItemUiData().copy(
                        name = "Euro",
                        code = "EUR"
                    ),
                    searchResultCurrency
                ),
                searchQuery = "dolla",
                selectedCurrency = createDefaultCurrencyItem()
                    .copy(currencyCode = "USD")
            )

        // WHEN
        val filteredCurrencyData = selectCurrencyModel.getFilteredCurrencyListDataTransformedFlow(
            flowOf(data)
        ).first()

        // THEN
        with(filteredCurrencyData) {
            assertEquals(1, currencyList.size)
            assertEquals(searchResultCurrency, currencyList.first())
            assertEquals(0, selectedItemPosition)
        }
    }

    @Test
    fun test_getFilteredCurrencyListDataTransformedFlow_searchByCountryCode() = runTest {
        // GIVEN
        val searchResultCurrency = createCurrencyListItemUiData().copy(
            name = "Tanzanian Shilling",
            code = "TZS"
        )
        val data = createSelectCurrencyData()
            .copy(
                currencyList = listOf(
                    searchResultCurrency,
                    createCurrencyListItemUiData().copy(
                        name = "Euro",
                        code = "EUR"
                    ),
                    createCurrencyListItemUiData().copy(
                        name = "US dollar",
                        code = "USD"
                    )
                ),
                searchQuery = "tz",
                selectedCurrency = createDefaultCurrencyItem()
                    .copy(currencyCode = "USD")
            )

        // WHEN
        val filteredCurrencyData = selectCurrencyModel.getFilteredCurrencyListDataTransformedFlow(
            flowOf(data)
        ).first()

        // THEN
        with(filteredCurrencyData) {
            assertEquals(1, currencyList.size)
            assertEquals(searchResultCurrency, currencyList.first())
            assertEquals(-1, selectedItemPosition)
        }
    }

    @Test
    fun test_getSelectedCurrencyItem() {
        // GIVEN
        val currencyItem = createDefaultCurrencyItem()
        whenever(currencyRepository.getSelectedCurrency())
            .thenReturn(currencyItem)

        // WHEN
        val selectedCurrency = selectCurrencyModel.getSelectedCurrencyItem()

        // THEN
        assertEquals(currencyItem, selectedCurrency)
    }

    @Test
    fun test_selectCurrencyItem() = runTest {
        // GIVEN
        val currencyItem = createDefaultCurrencyItem()

        // WHEN
        selectCurrencyModel.selectCurrencyItem(currencyItem)

        // THEN
        verify(currencyRepository).selectCurrency(currencyItem)
    }

    @Test
    fun test_loadCurrencies() = runTest {
        // GIVEN
        val currencyList = listOf(
            createDefaultCurrencyItem().copy(displayName = "USD"),
            createDefaultCurrencyItem().copy(displayName = "EUR"),
            createDefaultCurrencyItem().copy(displayName = "UAH")
        ).toSet()
        whenever(currencyRepository.getAvailableCurrencies())
            .thenReturn(currencyList)

        // WHEN
        val loadedCurrencies = selectCurrencyModel.loadCurrencies()

        // THEN
        val sortedCurrencies = loadedCurrencies.sortedBy { it.displayName }.toSet()
        assertEquals(sortedCurrencies, loadedCurrencies)
    }

    private fun createSelectCurrencyData(): SelectCurrencyData {
        return SelectCurrencyData(
            currencyList = listOf(createCurrencyListItemUiData()),
            searchQuery = "",
            selectedCurrency = createDefaultCurrencyItem()
        )
    }

    private fun createCurrencyListItemUiData(): CurrencyListItemUiData {
        return CurrencyListItemUiData(
            code = "USD",
            symbol = "$",
            name = "US Dollar",
            isSelected = false,
            clickData = createDefaultCurrencyItem(),
            clickListener = {}
        )
    }

    private fun createDefaultCurrencyItem(): CurrencyItem {
        return CurrencyItem(
            currencyCode = "USD",
            symbol = "$",
            displayName = "US Dollar",
            defaultFractionDigits = 2
        )
    }
}