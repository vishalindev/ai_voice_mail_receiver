package com.example.ai_voice_mail_receiver

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_METHODS)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "updateIspNumbers" -> {
                        val numbers = call.argument<List<String>>("numbers").orEmpty()
                        PreferencesStore.setIspNumbers(this, numbers)
                        result.success(null)
                    }
                    "setAutoHandling" -> {
                        val enabled = call.argument<Boolean>("enabled") ?: false
                        PreferencesStore.setAutoHandling(this, enabled)
                        result.success(null)
                    }
                    "requestPermissions" -> {
                        requestAppPermissions()
                        result.success(null)
                    }
                    else -> result.notImplemented()
                }
            }

        EventChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_EVENTS)
            .setStreamHandler(EventStreamHandler)
    }

    private fun requestAppPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SEND_SMS,
            Manifest.permission.POST_NOTIFICATIONS,
        )
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 99)
        }
    }

    companion object {
        const val CHANNEL_METHODS = "isp_auto_handler/methods"
        const val CHANNEL_EVENTS = "isp_auto_handler/events"
    }
}
