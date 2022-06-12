package com.example.tipjar.data.currency

import com.example.tipjar.data.currency.model.CurrencyItem
import com.example.tipjar.sharedpref.IAppSharedPref
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_CURRENCY_CODE = "USD"

@Singleton
internal class CurrencyRepository @Inject constructor(
    private val appSharedPref: IAppSharedPref
) : ICurrencyRepository {

    private val _currencySelectionUpdatedFlow = MutableSharedFlow<CurrencyItem>()
    override val currencySelectionUpdatedFlow: SharedFlow<CurrencyItem> = _currencySelectionUpdatedFlow

    override suspend fun selectCurrency(currencyItem: CurrencyItem) {
        appSharedPref.setSelectedCurrencyId(currencyItem.currencyCode)

        _currencySelectionUpdatedFlow.emit(currencyItem)
    }

    override fun getSelectedCurrency(): CurrencyItem {
        val currencyCode = appSharedPref.getSelectedCurrencyId() ?: DEFAULT_CURRENCY_CODE

        return getCurrencyItemFromCode(currencyCode)
    }

    override fun getAvailableCurrencies(): Set<CurrencyItem> {
        return Currency.getAvailableCurrencies().mapNotNull {
            it.asCurrencyItem()
        }.toSet()
    }

    private fun getCurrencyItemFromCode(code: String) : CurrencyItem {
        val currency = Currency.getInstance(code)

        return currency.asCurrencyItem()
    }

    private fun Currency.asCurrencyItem(): CurrencyItem {
        return CurrencyItem(
            currencyCode = currencyCode,
            symbol = symbol,
            displayName = displayName,
            defaultFractionDigits = defaultFractionDigits
        )
    }
}