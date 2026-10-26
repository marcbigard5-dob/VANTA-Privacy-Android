# Engineering roadmap

## M1 — Honest status dashboard
- [x] Compose dashboard
- [x] Network transport status
- [x] Permission-aware Bluetooth state
- [x] Deterministic policy-engine foundation
- [ ] Run Gradle build and unit tests in Android Studio

## M2 — Wi-Fi / Bluetooth observations
- Add version-aware callbacks and tests.
- Avoid collecting BSSIDs, device identifiers, or nearby-device inventories unless strictly needed.
- Surface confidence and unavailable-data states.

## M3 — Content inspection
- User-selected files via Storage Access Framework.
- URL parser and local checks.
- File size/time limits and safe quarantine storage.
- Never execute imported files.

## M4 — VPN monitor
- Implement VpnService TUN lifecycle and packet engine.
- Decide and document behavior on service failure.
- Test IPv4, IPv6, DNS, UDP, TCP, and battery impact.
- No TLS interception by default.

## M5 — Security review
- Verify no content appears in logs.
- Verify no unexpected outbound traffic.
- Dependency and manifest review.
- Device tests across supported Android versions.
