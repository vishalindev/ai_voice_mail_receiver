package com.example.ai_voice_mail_receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import java.io.File
import java.time.Instant

class AutoHandleForegroundService : Service() {
    private var recorder: MediaRecorder? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val number = intent?.getStringExtra(EXTRA_PHONE_NUMBER) ?: "Unknown"
        startForeground(NOTIFICATION_ID, buildNotification(number))

        val recordingPath = tryRecord(number)
        trySendSms(number)
        NotificationHelper.showHandledNotification(this, number, recordingPath)

        EventStreamHandler.emit(
            mapOf(
                "phoneNumber" to number,
                "timestamp" to Instant.now().toString(),
                "recordingPath" to recordingPath,
                "status" to if (recordingPath == null) "handled_no_recording" else "handled_recorded",
            )
        )

        stopSelf()
        return START_NOT_STICKY
    }

    private fun tryRecord(number: String): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ limitations: call audio recording may be blocked by device policy.
            }
            val dir = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "isp_recordings")
            if (!dir.exists()) dir.mkdirs()
            val output = File(dir, "call_${number.filter { it.isDigit() }}_${System.currentTimeMillis()}.m4a")

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(this) else MediaRecorder()
            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(output.absolutePath)
                prepare()
                start()
                Thread.sleep(5000)
                stop()
                release()
            }
            recorder = null
            output.absolutePath
        } catch (_: Exception) {
            recorder?.release()
            recorder = null
            null
        }
    }

    private fun trySendSms(number: String) {
        runCatching {
            SmsManager.getDefault().sendTextMessage(
                number,
                null,
                SMS_TEMPLATE,
                null,
                null,
            )
        }
    }

    private fun buildNotification(number: String): Notification {
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "ISP Auto Handler",
                NotificationManager.IMPORTANCE_LOW,
            )
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_call_mute)
            .setContentTitle("Handling ISP call")
            .setContentText("Rejected and responding to $number")
            .setOngoing(true)
            .build()
    }

    companion object {
        const val EXTRA_PHONE_NUMBER = "extra_phone_number"
        private const val NOTIFICATION_ID = 301
        private const val CHANNEL_ID = "isp_auto_handler_fg"
        private const val SMS_TEMPLATE =
            "Hi, I am currently unavailable. Please leave a voicemail or message your issue."
    }
}
