package com.example.ai_voice_mail_receiver

import android.content.Intent
import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService

class MyCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val incoming = callDetails.handle?.schemeSpecificPart ?: "unknown"
        EventStreamHandler.emit("Incoming call detected: $incoming")

        if (!PreferencesStore.isAutoResponderEnabled(this)) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val intent = Intent(this, AutoAnswerService::class.java).apply {
            putExtra(AutoAnswerService.EXTRA_NUMBER, incoming)
            data = Uri.parse("tel:$incoming")
        }
        startForegroundService(intent)

        respondToCall(callDetails, CallResponse.Builder().build())
    }
}
