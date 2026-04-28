import 'dart:async';

import 'package:flutter/services.dart';

class NativeBridge {
  NativeBridge._();
  static final NativeBridge instance = NativeBridge._();

  static const _methodChannel = MethodChannel('auto_responder/methods');
  static const _eventChannel = EventChannel('auto_responder/events');

  Stream<String> eventStream() {
    return _eventChannel
        .receiveBroadcastStream()
        .map((event) => event?.toString() ?? 'Unknown event');
  }

  Future<void> setAutoResponderEnabled(bool enabled) {
    return _methodChannel
        .invokeMethod('setAutoResponderEnabled', {'enabled': enabled});
  }

  Future<void> requestDialerRole() {
    return _methodChannel.invokeMethod('requestDialerRole');
  }

  Future<void> requestPermissions() {
    return _methodChannel.invokeMethod('requestPermissions');
  }
}
