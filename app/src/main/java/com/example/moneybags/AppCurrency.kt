package com.example.moneybags

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

enum class AppCurrency(val labelRes: Int) {
    USD(R.string.currency_usd),
    EUR(R.string.currency_eur),
    JPY(R.string.currency_jpy),
    GBP(R.string.currency_gbp);

    val symbol: String
        get() = Currency.getInstance(name).getSymbol(Locale.US)

    fun formatter(): NumberFormat = NumberFormat.getCurrencyInstance(Locale.US).apply {
        currency = Currency.getInstance(this@AppCurrency.name)
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
}
