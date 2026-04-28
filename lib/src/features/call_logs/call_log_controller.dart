import 'dart:async';

import 'package:flutter/foundation.dart';

import '../../data/call_log_entry.dart';
import '../../data/local_database.dart';
import '../../platform/native_bridge.dart';

class CallLogController extends ChangeNotifier {
  final List<CallLogEntry> logs = [];
  StreamSubscription<Map<String, dynamic>>? _subscription;

  Future<void> init() async {
    await _refresh();
    _subscription = NativeBridge.instance.eventStream().listen((event) async {
      await LocalDatabase.instance.insertLog(
        CallLogEntry(
          id: null,
          phoneNumber: (event['phoneNumber'] ?? 'Unknown').toString(),
          timestamp: DateTime.tryParse(event['timestamp']?.toString() ?? '') ??
              DateTime.now(),
          recordingPath: event['recordingPath']?.toString(),
          status: (event['status'] ?? 'handled').toString(),
        ),
      );
      await _refresh();
    });
  }

  Future<void> _refresh() async {
    logs
      ..clear()
      ..addAll(await LocalDatabase.instance.fetchLogs());
    notifyListeners();
  }

  @override
  void dispose() {
    _subscription?.cancel();
    super.dispose();
  }
}
