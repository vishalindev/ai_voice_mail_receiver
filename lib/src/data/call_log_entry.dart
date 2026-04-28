class CallLogEntry {
  CallLogEntry({
    required this.id,
    required this.phoneNumber,
    required this.timestamp,
    required this.recordingPath,
    required this.status,
  });

  final int? id;
  final String phoneNumber;
  final DateTime timestamp;
  final String? recordingPath;
  final String status;

  Map<String, Object?> toMap() {
    return {
      'id': id,
      'phone_number': phoneNumber,
      'timestamp': timestamp.toIso8601String(),
      'recording_path': recordingPath,
      'status': status,
    };
  }

  factory CallLogEntry.fromMap(Map<String, Object?> map) {
    return CallLogEntry(
      id: map['id'] as int?,
      phoneNumber: map['phone_number'] as String,
      timestamp: DateTime.parse(map['timestamp'] as String),
      recordingPath: map['recording_path'] as String?,
      status: map['status'] as String,
    );
  }
}
