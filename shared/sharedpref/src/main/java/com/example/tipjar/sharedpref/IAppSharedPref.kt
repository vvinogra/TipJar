package com.example.tipjar.sharedpref

interface IAppSharedPref {
    fun setSelectedCurrencyId(currencyId: String)
    fun getSelectedCurrencyId(): String?
}