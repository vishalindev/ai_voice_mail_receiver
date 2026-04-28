package com.example.ai_voice_mail_receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telecom.TelecomManager
import androidx.core.app.NotificationCompat

class AutoAnswerService : Service() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!PreferencesStore.isAutoResponderEnabled(this)) {
            stopSelf()
            return START_NOT_STICKY
        }

        val number = intent?.getStringExtra(EXTRA_NUMBER) ?: "unknown"
        val immediate = intent?.getBooleanExtra(EXTRA_IMMEDIATE, false) ?: false

        startForeground(NOTIFICATION_ID, buildNotification(number))

        val delayMs = if (immediate) 0L else 15_000L
        handler.postDelayed({
            answerCallAndPlayPrompt(number)
            stopSelf()
        }, delayMs)

        return START_NOT_STICKY
    }

    private fun answerCallAndPlayPrompt(number: String) {
        val telecom = getSystemService(Context.TELECOM_SERVICE) as TelecomManager
        runCatching {
            telecom.acceptRingingCall()
            EventStreamHandler.emit("Auto-answered call from $number")
            playInstruction()
        }.onFailure {
            EventStreamHandler.emit("Auto-answer failed for $number: ${it.message}")
        }
    }

    private fun playInstruction() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = true

        val resId = resources.getIdentifier("voicemail_instruction", "raw", packageName)
        if (resId == 0) {
            EventStreamHandler.emit("Missing raw/voicemail_instruction.mp3 resource")
            return
        }

        val player = MediaPlayer.create(this, resId)
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        player.setOnCompletionListener {
            it.release()
            audioManager.isSpeakerphoneOn = false
            audioManager.mode = AudioManager.MODE_NORMAL
            EventStreamHandler.emit("Played voicemail instruction")
        }
        player.start()
    }

    private fun buildNotification(number: String): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Auto responder call monitoring",
                NotificationManager.IMPORTANCE_LOW,
            )
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle("Auto-responder active")
            .setContentText("Monitoring call from $number")
            .setOngoing(true)
            .build()
    }

    companion object {
        const val EXTRA_NUMBER = "extra_number"
        const val EXTRA_IMMEDIATE = "extra_immediate"
        private const val CHANNEL_ID = "auto_answer_phone_call"
        private const val NOTIFICATION_ID = 4421
    }
}
