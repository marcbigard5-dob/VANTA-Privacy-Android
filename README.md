# VANTA Privacy — Android prototype

VANTA is a local-first Android privacy utility prototype built with Kotlin and Jetpack Compose. This repository is under active development; it is **not** a complete antivirus, firewall, or device-protection product.

## Implemented in this build

- **Device status dashboard:** reads the active network transport and reports Bluetooth enabled/disabled state when Android permission allows it.
- **Local file fingerprint:** lets the user select a file using Android's system picker and computes its SHA-256 hash on-device. The app does not upload the file.
- **Local URL structure check:** flags selected warning signs such as HTTP, URL user-info, punycode labels, known shorteners, malformed addresses, and unusually long URLs. It does not open or contact the submitted URL.
- **Build automation:** GitHub Actions compiles a debug APK and runs JVM unit tests.

## Important limitations

- A SHA-256 fingerprint is **not** malware detection and does not establish that a file is safe.
- URL checks are heuristic only. There is no live threat-intelligence or reputation lookup, and a URL with no warning may still be malicious.
- There is no VPN packet processing, firewall enforcement, automatic traffic blocking, Bluetooth packet inspection, comprehensive Wi-Fi risk engine, quarantine, encrypted vault, notification inspection, or VANTA DSL runtime.
- Network status reporting does not prove that a connection is private or secure.

The UI labels these limitations explicitly. Do not market this prototype as providing protection that has not been implemented and tested.

## Build locally

1. Open the repository in Android Studio.
2. Use JDK 17 and Android SDK Platform 35.
3. Allow Gradle sync to finish.
4. Run the `app` configuration on an Android device or emulator.

GitHub Actions builds the debug APK from the workflow and retains it as the `VANTA-debug-apk` artifact for seven days.

## Privacy and permission principles

- No analytics SDK or remote file/URL analysis endpoint is included.
- File analysis starts only after the user selects a file.
- URL analysis is performed locally and must not make a network request.
- Permissions should be requested only when the related feature is used.
- Never report a feature as active protection unless its implementation and tests demonstrate that claim.

## Current milestone checks

- [x] Compose dashboard and device-status refresh.
- [x] Permission-aware Bluetooth status.
- [x] Local SHA-256 fingerprint tool.
- [x] Local URL structure warnings.
- [x] Automated debug build and unit-test workflow.
- [ ] Instrumented UI tests on a real device/emulator.
- [ ] Independently reviewed security architecture.
- [ ] Properly designed and tested VPN/firewall feature (not yet implemented).
