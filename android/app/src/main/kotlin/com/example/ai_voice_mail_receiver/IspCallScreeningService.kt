package com.example.ai_voice_mail_receiver

import android.content.Intent
import android.net.Uri
import android.telecom.Call
import android.telecom.CallScreeningService

class IspCallScreeningService : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        val handle = callDetails.handle
        val number = handle?.schemeSpecificPart ?: "Unknown"

        val shouldHandle =
            PreferencesStore.isAutoHandlingEnabled(this) &&
                PreferencesStore.hasMatchingNumber(this, number)

        if (!shouldHandle) {
            respondToCall(callDetails, CallResponse.Builder().build())
            return
        }

        val response = CallResponse.Builder()
            .setDisallowCall(true)
            .setRejectCall(true)
            .setSkipCallLog(false)
            .setSkipNotification(true)
            .build()
        respondToCall(callDetails, response)

        val serviceIntent = Intent(this, AutoHandleForegroundService::class.java).apply {
            data = Uri.parse("tel:$number")
            putExtra(AutoHandleForegroundService.EXTRA_PHONE_NUMBER, number)
        }
        startForegroundService(serviceIntent)
    }
}
