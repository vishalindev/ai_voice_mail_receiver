package com.example.ai_voice_mail_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager

class CallStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (!PreferencesStore.isAutoResponderEnabled(context)) return

        val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
        val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ?: "unknown"

        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> {
                val now = System.currentTimeMillis()
                val immediate = PreferencesStore.isSecondMissedWithinFiveMinutes(context, number, now)
                val serviceIntent = Intent(context, AutoAnswerService::class.java).apply {
                    putExtra(AutoAnswerService.EXTRA_NUMBER, number)
                    putExtra(AutoAnswerService.EXTRA_IMMEDIATE, immediate)
                }
                context.startForegroundService(serviceIntent)
                EventStreamHandler.emit("RINGING from $number (immediate=$immediate)")
            }

            TelephonyManager.EXTRA_STATE_IDLE -> {
                PreferencesStore.markMissedCall(context, number, System.currentTimeMillis())
                EventStreamHandler.emit("Call moved to IDLE: $number")
            }

            TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                EventStreamHandler.emit("Call answered/offhook: $number")
            }
        }
    }
}
