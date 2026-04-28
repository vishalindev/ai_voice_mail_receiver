# AI Voice Mail Receiver (Flutter + Android Native)

This project provides a Flutter UI with Android-native call screening logic for ISP calls.

## Features

- Toggle automatic ISP call handling.
- Save multiple ISP phone numbers.
- Native `CallScreeningService` rejects matching calls.
- Sends auto SMS after rejection:
  - "Hi, I am currently unavailable. Please leave a voicemail or message your issue."
- Attempts short call-adjacent recording (device-policy dependent on Android 10+).
- Stores call handling logs in SQLite via Flutter (`sqflite`).
- Streams native events to Flutter UI through `MethodChannel` + `EventChannel`.
- Playback feature for local recordings from Flutter UI.
- Notification when ISP calls are auto-handled.

## Project Structure

- `lib/`: Flutter app, controllers, database, UI, platform bridge.
- `android/app/src/main/kotlin/...`: Native services and handlers.

## Android Setup

1. Open app settings and set this app as the default **Call Screening app** on Android.
2. Grant required permissions at runtime.
3. Exempt app from aggressive battery optimization if device vendor kills background services.

## Required Permissions

Declared in `AndroidManifest.xml`:

- `READ_PHONE_STATE`
- `ANSWER_PHONE_CALLS`
- `READ_CALL_LOG`
- `RECORD_AUDIO`
- `SEND_SMS`
- `FOREGROUND_SERVICE`
- `POST_NOTIFICATIONS`

> Note: On Android 10+ and some OEM devices, call audio capture may be blocked. App logs fallback status as `handled_no_recording` when recording is unavailable.

## Flutter ↔ Native Bridge

- Method channel: `isp_auto_handler/methods`
  - `updateIspNumbers`
  - `setAutoHandling`
  - `requestPermissions`
- Event channel: `isp_auto_handler/events`
  - Emits `phoneNumber`, `timestamp`, `recordingPath`, `status`

## Run

```bash
flutter pub get
flutter run
```
