import 'package:flutter/material.dart';

import 'features/call_logs/call_log_controller.dart';
import 'features/call_logs/call_log_page.dart';
import 'features/settings/settings_controller.dart';

class AiVoiceMailApp extends StatefulWidget {
  const AiVoiceMailApp({super.key});

  @override
  State<AiVoiceMailApp> createState() => _AiVoiceMailAppState();
}

class _AiVoiceMailAppState extends State<AiVoiceMailApp> {
  late final SettingsController _settingsController;
  late final CallLogController _callLogController;

  @override
  void initState() {
    super.initState();
    _settingsController = SettingsController()..init();
    _callLogController = CallLogController()..init();
  }

  @override
  void dispose() {
    _settingsController.dispose();
    _callLogController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'AI Voice Mail Receiver',
      theme: ThemeData(useMaterial3: true, colorSchemeSeed: Colors.teal),
      home: CallLogPage(
        settingsController: _settingsController,
        callLogController: _callLogController,
      ),
    );
  }
}
