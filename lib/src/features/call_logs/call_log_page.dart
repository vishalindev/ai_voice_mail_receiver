import 'package:flutter/material.dart';

import '../settings/settings_controller.dart';
import 'call_log_controller.dart';

class CallLogPage extends StatefulWidget {
  const CallLogPage({
    super.key,
    required this.settingsController,
    required this.callLogController,
  });

  final SettingsController settingsController;
  final CallLogController callLogController;

  @override
  State<CallLogPage> createState() => _CallLogPageState();
}

class _CallLogPageState extends State<CallLogPage> {
  late final TextEditingController _logsController;

  @override
  void initState() {
    super.initState();
    _logsController = TextEditingController();
    widget.settingsController.addListener(_refresh);
    widget.callLogController.addListener(_refresh);
    _refresh();
  }

  void _refresh() {
    _logsController.text = widget.callLogController.logsAsText;
    if (mounted) setState(() {});
  }

  @override
  void dispose() {
    widget.settingsController.removeListener(_refresh);
    widget.callLogController.removeListener(_refresh);
    _logsController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final settings = widget.settingsController;
    return Scaffold(
      appBar: AppBar(title: const Text('Personal Auto Responder')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            SwitchListTile(
              value: settings.enabled,
              onChanged: settings.setEnabled,
              title: const Text('Enable Auto-Responder'),
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton(
                    onPressed: settings.requestDialerRole,
                    child: const Text('Request ROLE_DIALER'),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: OutlinedButton(
                    onPressed: settings.requestPermissions,
                    child: const Text('Request Permissions'),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _logsController,
              maxLines: 14,
              readOnly: true,
              decoration: const InputDecoration(
                labelText: 'Call logs',
                border: OutlineInputBorder(),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
