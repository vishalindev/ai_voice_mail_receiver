package com.example.ai_voice_mail_receiver

import android.content.Context

object PreferencesStore {
    private const val PREFS = "isp_auto_handler"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_NUMBERS = "numbers"

    fun setAutoHandling(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun isAutoHandlingEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    fun setIspNumbers(context: Context, numbers: List<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putStringSet(KEY_NUMBERS, numbers.toSet()).apply()
    }

    fun hasMatchingNumber(context: Context, incoming: String?): Boolean {
        if (incoming.isNullOrBlank()) return false
        val set = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getStringSet(KEY_NUMBERS, emptySet()).orEmpty()
        val normalizedIncoming = incoming.filter { it.isDigit() }
        return set.any { stored ->
            val normalizedStored = stored.filter { it.isDigit() }
            normalizedIncoming.endsWith(normalizedStored) ||
                normalizedStored.endsWith(normalizedIncoming)
        }
    }
}
