import 'dart:async';

import 'package:flutter/services.dart';

class NativeBridge {
  NativeBridge._();
  static final NativeBridge instance = NativeBridge._();

  static const _methodChannel = MethodChannel('isp_auto_handler/methods');
  static const _eventChannel = EventChannel('isp_auto_handler/events');

  Stream<Map<String, dynamic>> eventStream() {
    return _eventChannel
        .receiveBroadcastStream()
        .map((event) => Map<String, dynamic>.from(event as Map));
  }

  Future<void> updateIspNumbers(List<String> numbers) {
    return _methodChannel.invokeMethod('updateIspNumbers', {'numbers': numbers});
  }

  Future<void> setAutoHandling(bool enabled) {
    return _methodChannel.invokeMethod('setAutoHandling', {'enabled': enabled});
  }

  Future<void> requestNativePermissions() {
    return _methodChannel.invokeMethod('requestPermissions');
  }
}
