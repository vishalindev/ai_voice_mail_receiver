import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../platform/native_bridge.dart';

class SettingsController extends ChangeNotifier {
  bool enabled = false;

  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    enabled = prefs.getBool('autoResponderEnabled') ?? false;
    await NativeBridge.instance.setAutoResponderEnabled(enabled);
    notifyListeners();
  }

  Future<void> setEnabled(bool value) async {
    enabled = value;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('autoResponderEnabled', enabled);
    await NativeBridge.instance.setAutoResponderEnabled(enabled);
    notifyListeners();
  }

  Future<void> requestDialerRole() => NativeBridge.instance.requestDialerRole();

  Future<void> requestPermissions() => NativeBridge.instance.requestPermissions();
}
