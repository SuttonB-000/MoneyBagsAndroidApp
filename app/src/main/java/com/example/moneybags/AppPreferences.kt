package com.example.moneybags

import android.content.Context

object AppPreferences {
    private const val PREFERENCES_NAME = "money_bags_settings"
    private const val DARK_MODE_KEY = "dark_mode"
    private const val TEXT_SCALE_KEY = "text_scale"
    private const val CURRENCY_KEY = "currency"
    const val MIN_TEXT_SCALE = 0.9f
    const val MAX_TEXT_SCALE = 1.5f
    const val DEFAULT_TEXT_SCALE = 1.0f

    fun isDarkModeEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getBoolean(DARK_MODE_KEY, false)

    fun setDarkModeEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(DARK_MODE_KEY, enabled)
            .apply()
    }

    fun getTextScale(context: Context): Float {
        val savedScale = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getFloat(TEXT_SCALE_KEY, DEFAULT_TEXT_SCALE)
        return if (savedScale.isFinite()) {
            savedScale.coerceIn(MIN_TEXT_SCALE, MAX_TEXT_SCALE)
        } else {
            DEFAULT_TEXT_SCALE
        }
    }

    fun setTextScale(context: Context, scale: Float) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putFloat(TEXT_SCALE_KEY, scale.coerceIn(MIN_TEXT_SCALE, MAX_TEXT_SCALE))
            .apply()
    }

    fun getCurrency(context: Context): AppCurrency {
        val savedCode = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(CURRENCY_KEY, AppCurrency.USD.name)
        return AppCurrency.entries.firstOrNull { it.name == savedCode } ?: AppCurrency.USD
    }

    fun setCurrency(context: Context, currency: AppCurrency) {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(CURRENCY_KEY, currency.name)
            .apply()
    }
}
