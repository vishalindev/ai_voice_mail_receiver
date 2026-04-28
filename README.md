# Flutter Auto-Responder (Android)

This project is a Flutter + Kotlin Android implementation of a personal auto-answering machine.

## What it does

- Flutter UI has:
  - Toggle: **Enable Auto-Responder**
  - Read-only text field showing call logs/events
- Android native uses:
  - `CallScreeningService` to detect incoming calls
  - Broadcast receiver to track call state transitions
  - Foreground service (`phoneCall` type) to survive ringing window
  - `TelecomManager.acceptRingingCall()` when conditions match
  - `MediaPlayer` + `AudioManager.MODE_IN_COMMUNICATION` to play instruction audio

## Auto-answer logic

When enabled:
1. Incoming call is detected.
2. App auto-answers if either:
   - call rings for ~15 seconds (~4 rings), or
   - same number has a second missed call within 5 minutes.
3. After answer, app plays `voicemail_instruction.mp3` (Android `res/raw/voicemail_instruction.mp3`).

Prompt text used by design:

> "This is an automated service for Vishal. I cannot take your call right now, please leave a message."

## Important Android constraints (Android 10+ through Android 16)

- App must be granted **ROLE_DIALER** by user, otherwise `acceptRingingCall()` usually fails.
- Android security does not allow direct injection of MP3 into cellular uplink microphone path.
- This implementation uses the practical workaround:
  - answer call
  - set communication audio mode
  - play audio through speaker/communication route
- Foreground service restrictions on modern Android require `foregroundServiceType="phoneCall"`.

## Manifest permissions included

- `READ_PHONE_STATE`
- `ANSWER_PHONE_CALLS`
- `MODIFY_AUDIO_SETTINGS`
- `BIND_SCREENING_SERVICE`
- `MANAGE_OWN_CALLS`
- `RECORD_AUDIO`
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_PHONE_CALL`
- `POST_NOTIFICATIONS`

## Flutter ↔ Native MethodChannel API

Method channel: `auto_responder/methods`

- `setAutoResponderEnabled({enabled: bool})`
- `requestDialerRole()`
- `requestPermissions()`

Event channel: `auto_responder/events`

- Emits timestamped log lines to show in Flutter text field.

## Setup

1. Add your MP3 at: `android/app/src/main/res/raw/voicemail_instruction.mp3`
2. Install and run app.
3. Tap **Request ROLE_DIALER** and approve.
4. Tap **Request Permissions** and grant all requested permissions.
5. Enable **Auto-Responder**.
