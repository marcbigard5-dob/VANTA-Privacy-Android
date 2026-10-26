# VANTA Privacy — Android starter v0.1.0

## Current scope

This repository is the first Android project scaffold for VANTA Privacy. It includes:
- Kotlin + Jetpack Compose dashboard.
- Local read-only network transport status.
- Basic Bluetooth enabled/disabled status with permission-aware behavior.
- A deterministic policy-engine foundation and unit tests.
- Explicit UI labels showing features that are not implemented.

## Not implemented yet

- VPN packet processing / firewall.
- Bluetooth packet inspection or automatic device blocking.
- Comprehensive Wi-Fi risk analysis.
- File/URL scanning and quarantine.
- Encryption vault.
- Notification inspection.
- VANTA DSL parser.

Do not describe this build as a complete security product. It currently reports limited device status only.

## Build

Open the folder in Android Studio with Android SDK 35 and JDK 17 available. Let Gradle sync, then run the `app` configuration on an Android device or emulator.

If your environment does not have the Android SDK or Gradle available, the project cannot be built there until those are installed.

## Privacy principles

- No analytics SDKs or cloud-analysis endpoints are included.
- No file contents are collected.
- Permissions should be requested only when the corresponding feature is used.
- The app must not report a feature as protected unless its implementation and tests demonstrate that claim.

## First milestone acceptance

1. App opens and renders dashboard.
2. Network transport status refreshes locally.
3. Bluetooth status handles missing permission without crashing.
4. Policy engine tests pass.
5. No unexpected network requests are added by the app.
