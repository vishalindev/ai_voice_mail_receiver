package com.example.ai_voice_mail_receiver

import io.flutter.plugin.common.EventChannel

object EventStreamHandler : EventChannel.StreamHandler {
    private var sink: EventChannel.EventSink? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        sink = events
    }

    override fun onCancel(arguments: Any?) {
        sink = null
    }

    fun emit(event: String) {
        sink?.success("${System.currentTimeMillis()}: $event")
    }
}
