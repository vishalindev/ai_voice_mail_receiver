import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';
import 'package:sqflite/sqflite.dart';

import 'call_log_entry.dart';

class LocalDatabase {
  static final LocalDatabase instance = LocalDatabase._();
  LocalDatabase._();

  Database? _db;

  Future<Database> get db async {
    if (_db != null) return _db!;
    final dir = await getApplicationSupportDirectory();
    final path = p.join(dir.path, 'call_logs.db');
    _db = await openDatabase(
      path,
      version: 1,
      onCreate: (database, version) async {
        await database.execute('''
          CREATE TABLE call_logs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            phone_number TEXT NOT NULL,
            timestamp TEXT NOT NULL,
            recording_path TEXT,
            status TEXT NOT NULL
          );
        ''');
      },
    );
    return _db!;
  }

  Future<void> insertLog(CallLogEntry entry) async {
    final database = await db;
    await database.insert('call_logs', entry.toMap());
  }

  Future<List<CallLogEntry>> fetchLogs() async {
    final database = await db;
    final rows = await database.query(
      'call_logs',
      orderBy: 'timestamp DESC',
    );
    return rows.map(CallLogEntry.fromMap).toList();
  }
}
