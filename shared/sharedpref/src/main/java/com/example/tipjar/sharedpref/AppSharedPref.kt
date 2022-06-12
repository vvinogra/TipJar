package com.example.tipjar.sharedpref

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREF_NAME = "com.example.tipjar.data.sharedprefs.appsharedpref"
private const val PREF_SELECTED_CURRENCY = "pref_selected_currency"

@Singleton
internal class AppSharedPref @Inject constructor(
    @ApplicationContext private val context: Context
) : IAppSharedPref {
    private val prefs by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun setSelectedCurrencyId(currencyId: String) {
       prefs.edit {
           putString(PREF_SELECTED_CURRENCY, currencyId)
       }
    }

    override fun getSelectedCurrencyId() = prefs.getString(PREF_SELECTED_CURRENCY, null)


}