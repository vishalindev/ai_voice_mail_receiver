package com.example.ai_voice_mail_receiver

import android.content.Context

object PreferencesStore {
    private const val PREFS = "auto_responder"
    private const val KEY_ENABLED = "enabled"

    fun setAutoResponderEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun isAutoResponderEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    fun markMissedCall(context: Context, number: String, timestampMs: Long) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString("missed_number", number)
            .putLong("missed_at", timestampMs)
            .apply()
    }

    fun isSecondMissedWithinFiveMinutes(context: Context, number: String, nowMs: Long): Boolean {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val lastNumber = prefs.getString("missed_number", null)
        val lastAt = prefs.getLong("missed_at", 0L)
        return lastNumber == number && (nowMs - lastAt) <= 5 * 60 * 1000
    }
}
