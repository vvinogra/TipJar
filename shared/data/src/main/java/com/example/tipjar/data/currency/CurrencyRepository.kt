package com.example.tipjar.data.currency

import com.example.tipjar.data.currency.model.CurrencyItem
import com.example.tipjar.data.currency.model.SupportedCurrencyCodes
import com.example.tipjar.sharedpref.IAppSharedPref
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

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

        return this.getCurrencyItemFromCode(currencyCode)
    }

    override fun getCurrencyItemFromCode(code: String): CurrencyItem {
        val currency = Currency.getInstance(code)

        return currency.asCurrencyItem()
    }

    override fun getAvailableCurrencies(): Set<CurrencyItem> {
        return SupportedCurrencyCodes.values().mapNotNull {
            getCurrencyItemFromCodeSafe(it.toString())
        }.toSet()
    }

    private fun getCurrencyItemFromCodeSafe(code: String): CurrencyItem? {
        return try {
            getCurrencyItemFromCode(code)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun Currency.asCurrencyItem(): CurrencyItem {
        return CurrencyItem(
            currencyCode = currencyCode,
            symbol = symbol,
            displayName = displayName,
            // Some currencies may have a negative [defaultFractionDigits] value
            defaultFractionDigits = max(defaultFractionDigits, 0)
        )
    }
}