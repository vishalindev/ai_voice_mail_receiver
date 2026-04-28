import 'dart:async';

import 'package:flutter/foundation.dart';

import '../../platform/native_bridge.dart';

class CallLogController extends ChangeNotifier {
  final List<String> logs = [];
  StreamSubscription<String>? _subscription;

  Future<void> init() async {
    _subscription = NativeBridge.instance.eventStream().listen((event) {
      logs.insert(0, event);
      if (logs.length > 100) logs.removeLast();
      notifyListeners();
    });
  }

  String get logsAsText => logs.join('\n');

  @override
  void dispose() {
    _subscription?.cancel();
    super.dispose();
  }
}
