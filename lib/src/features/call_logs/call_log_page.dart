import 'package:audioplayers/audioplayers.dart';
import 'package:flutter/material.dart';

import '../../data/call_log_entry.dart';
import '../../platform/native_bridge.dart';
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
  late final TextEditingController _numbersController;
  final AudioPlayer _audioPlayer = AudioPlayer();

  @override
  void initState() {
    super.initState();
    _numbersController = TextEditingController();
    widget.settingsController.addListener(_onSettingsChange);
    widget.callLogController.addListener(_onLogsChange);
  }

  void _onSettingsChange() => setState(() {
        _numbersController.text = widget.settingsController.ispNumbers.join(', ');
      });

  void _onLogsChange() => setState(() {});

  @override
  void dispose() {
    widget.settingsController.removeListener(_onSettingsChange);
    widget.callLogController.removeListener(_onLogsChange);
    _numbersController.dispose();
    _audioPlayer.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final settings = widget.settingsController;
    final logs = widget.callLogController.logs;
    return Scaffold(
      appBar: AppBar(title: const Text('ISP Auto Call Handler')),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SwitchListTile(
              title: const Text('Enable ISP Auto Handling'),
              subtitle: const Text('Auto-reject + SMS + recording trigger'),
              value: settings.autoHandlingEnabled,
              onChanged: settings.toggleAutoHandling,
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _numbersController,
              maxLines: 3,
              decoration: const InputDecoration(
                border: OutlineInputBorder(),
                labelText: 'ISP numbers (comma/newline separated)',
              ),
            ),
            const SizedBox(height: 8),
            FilledButton(
              onPressed: () => settings.saveIspNumbers(_numbersController.text),
              child: const Text('Save ISP Numbers'),
            ),
            const SizedBox(height: 8),
            OutlinedButton(
              onPressed: () => NativeBridge.instance.requestNativePermissions(),
              child: const Text('Request Android Permissions'),
            ),
            const SizedBox(height: 16),
            const Text('Recorded Call Logs', style: TextStyle(fontSize: 16)),
            const SizedBox(height: 8),
            Expanded(
              child: logs.isEmpty
                  ? const Center(child: Text('No auto-handled calls yet.'))
                  : ListView.builder(
                      itemCount: logs.length,
                      itemBuilder: (context, index) =>
                          _CallLogTile(entry: logs[index], onPlay: _play),
                    ),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _play(String path) async {
    await _audioPlayer.stop();
    await _audioPlayer.play(DeviceFileSource(path));
  }
}

class _CallLogTile extends StatelessWidget {
  const _CallLogTile({required this.entry, required this.onPlay});

  final CallLogEntry entry;
  final Future<void> Function(String path) onPlay;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: ListTile(
        title: Text(entry.phoneNumber),
        subtitle: Text('${entry.status} • ${entry.timestamp.toLocal()}'),
        trailing: entry.recordingPath == null
            ? const Icon(Icons.info_outline)
            : IconButton(
                icon: const Icon(Icons.play_arrow),
                onPressed: () => onPlay(entry.recordingPath!),
              ),
      ),
    );
  }
}
