import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../platform/native_bridge.dart';

class SettingsController extends ChangeNotifier {
  bool autoHandlingEnabled = false;
  final List<String> ispNumbers = [];

  Future<void> init() async {
    final prefs = await SharedPreferences.getInstance();
    autoHandlingEnabled = prefs.getBool('autoHandlingEnabled') ?? false;
    ispNumbers
      ..clear()
      ..addAll(prefs.getStringList('ispNumbers') ?? []);
    await NativeBridge.instance.setAutoHandling(autoHandlingEnabled);
    await NativeBridge.instance.updateIspNumbers(ispNumbers);
    notifyListeners();
  }

  Future<void> toggleAutoHandling(bool enabled) async {
    autoHandlingEnabled = enabled;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('autoHandlingEnabled', enabled);
    await NativeBridge.instance.setAutoHandling(enabled);
    notifyListeners();
  }

  Future<void> saveIspNumbers(String rawInput) async {
    final next = rawInput
        .split(RegExp(r'[,\n]'))
        .map((value) => value.trim())
        .where((value) => value.isNotEmpty)
        .toSet()
        .toList();
    ispNumbers
      ..clear()
      ..addAll(next);
    final prefs = await SharedPreferences.getInstance();
    await prefs.setStringList('ispNumbers', ispNumbers);
    await NativeBridge.instance.updateIspNumbers(ispNumbers);
    notifyListeners();
  }
}
