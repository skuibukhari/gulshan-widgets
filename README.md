# Gulshan Factory — Android Home-Screen Widgets

Native Android home-screen widgets for the Gulshan Factory app
(`https://kashf.alwaysdata.net`). The app itself stays a PWA; this tiny
native shell exists only to provide **real launcher widgets**, which a PWA
cannot do.

## Widgets included (7, all separate)

| # | Widget | Shows | Tap opens |
|---|--------|-------|-----------|
| 1 | Admin: روزانہ آرڈر | Production date + cut-off countdown | `/#daily` |
| 2 | Admin: سپلائی / گاڑی | Today's date + cut-off reminder | `/#supply` |
| 3 | محکمہ: آئٹم وائز | Production date (Bread/Dry/Namkeen/Fresh) | `/#daily` |
| 4 | محکمہ: دکان وائز | Production date + countdown | `/#daily` |
| 5 | دکان: آرڈر یاددہانی | Live countdown to 20:00 cut-off; **red warning only in the last 60 minutes** | `/#daily` |
| 6 | گاڑی والا: ڈیوٹی | Duty date + countdown (Arif / Rauf) | app home |
| 7 | سپلائر: دکانیں | Date + reminder (e.g. Naveed) | app home |

All countdowns use the **Asia/Karachi** timezone. Widgets compute everything
on-device (no login needed); tapping a widget opens the matching web-app
section where live order data appears after login.

The launcher icon of this app simply opens the web app in the browser.

## How to build (GitHub Actions — no Android Studio needed)

1. Create a **new empty GitHub repository** (e.g. `gulshan-widgets`).
2. Push this folder's contents to its `main` branch:
   ```bash
   cd gulshan-widgets
   git init
   git add -A
   git commit -m "Gulshan widgets"
   git branch -M main
   git remote add origin https://github.com/<you>/gulshan-widgets.git
   git push -u origin main
   ```
3. Open the repo on GitHub → **Actions** tab → the *Build Widgets APK*
   workflow runs automatically on the push.
4. When it finishes (green ✓), open the run → **Artifacts** →
   download **`gulshan-widgets-apk`**.
5. Copy the APK to the phone, tap it to install (allow "install unknown
   apps" once). It is signed with the debug key — fine for personal use.
6. Long-press the home screen → **Widgets** → **Gulshan Widgets** → drag
   the widgets you need.

## Project notes

- Package: `com.gulshanfactory.widgets`, minSdk 26, targetSdk 34.
- **Zero external dependencies** — plain framework `Activity`,
  `AppWidgetProvider` and `RemoteViews` only, so dependency resolution
  cannot fail the build.
- Layouts use only RemoteViews-safe views (`LinearLayout`, `TextView`)
  plus a shape drawable background.
- The reminder widget refreshes every ~15 min via an inexact
  `AlarmManager` alarm (no exact-alarm permission needed); the others use
  the standard 30-minute `updatePeriodMillis`.
- The warning window (`WARNING_MINUTES = 60` in
  `WidgetHelper.java`) mirrors the admin's "final period" setting.

## If the build ever fails

- The old project failed for three reasons: resources were committed under
  `ref/` instead of `res/`, the launcher icon was missing, and — most
  importantly — the Android SDK was never installed in CI
  (`gradle/actions/setup-gradle` only installs Gradle, not the SDK).
- Do **not** "fix" the SDK step with `android-actions/setup-android@v3`:
  it is broken (it runs `sdkmanager tools`, but Google removed the legacy
  `tools` package, so the step exits 1). The `ubuntu-latest` runner already
  ships a full Android SDK — this workflow uses it directly via
  `$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager`.
- Keep JDK 17 + Gradle 8.9 + AGP 8.5.2 together (a tested-compatible trio);
  Gradle 9.x is not supported by AGP 8.5.2.
