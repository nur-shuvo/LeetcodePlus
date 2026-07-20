# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LeetcodePlus is an unofficial native Android LeetCode client (Kotlin, Jetpack Compose). It provides daily problem reminders, code submission, weekly goal tracking, user stats, a home-screen Glance widget, and YouTube solution videos.

**Package:** `com.byteutility.dev.leetcode.plus`
**Min SDK:** 24 | **Target/Compile SDK:** 35 | **JVM Target:** 17

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK (requires keystore.properties)
./gradlew testDebugUnitTest      # Run unit tests
./gradlew detekt                 # Run detekt static analysis (with auto-correct)
./gradlew connectedDebugAndroidTest  # Run instrumented tests
```

## Architecture

Single-module app using **Hilt** for DI, **Jetpack Compose** for UI, and **Kotlin Coroutines/Flow** for async.

### Layer Structure

- **`network/`** — Retrofit-based API layer. Uses custom `@ResponseFormat`/`@RequestFormat` annotations with a `JsonOrXmlConverter` to handle both JSON and XML responses. Base URL comes from `BuildConfig.BASE_URL` (configured via secrets-gradle-plugin in `local.properties`). Some endpoints hit the LeetCode API proxy; others call `leetcode.com` directly (submit, interpret) or `clist.by` (contests).
- **`data/repository/`** — Repository interfaces + implementations for each domain: `ProblemsRepository`, `UserDetailsRepository`, `WeeklyGoalRepository`, `CodeEditorSubmitRepository`. Bound via `RepositoryModule`.
- **`data/database/`** — Room database (`LeetcodeDatabase`) with a single entity (`WeeklyGoalEntity`).
- **`data/datastore/`** — Androidx DataStore for key-value preferences.
- **`data/worker/`** — WorkManager workers: `ReminderNotificationWorker`, `UserDetailsSyncWorker`, `ClearGoalWorker`, `ContestReminderWorker`. Hilt-injected via `HiltWorkerFactory`.
- **`ui/screens/`** — Each screen is a composable with its own ViewModel. Screens: home, login, leetcodelogin (WebView for cookie auth), problem details, allproblems, settings, targetset, targetstatus, solutions (YouTube), contest details, webview.
- **`ui/navigation/`** — Type-safe navigation using `@Serializable` route objects. `LeetCodePlusNavGraph` defines the nav graph; `LeetCodePlusNavigation` wraps `NavController` actions. Deep links support `leetcode.com/problems/{titleSlug}`.
- **`ui/codeEditSubmit/`** — Separate `CodeEditorSubmitActivity` using Rosemoe/Sora editor with TextMate grammars (loaded from `assets/textmate/`) for multi-language code editing and submission.
- **`glance/`** — Glance AppWidget for Problem of the Day.
- **`monitor/`** — Network monitoring.
- **`troubleshoot/`** — Troubleshooting/debug screen.

### Key Patterns

- Navigation routes are `@Serializable` objects/data classes in `LeetCodePlusNavigation.kt`
- LeetCode authentication uses WebView cookie capture (csrf token + session cookie), passed as headers for submit/interpret endpoints
- `LeetCodePlusApplication` initializes TextMate editor themes/grammars and AdMob on startup
- Repositories follow interface/impl pattern, all provided as singletons via Hilt

## Linting

Detekt is configured in `detekt.yml` with auto-correct enabled. Key rules to be aware of:
- `MaxLineLength`: 120 chars (excludes `**/ui/**`)
- `LongMethod`: 60 lines (excludes `**/ui/**`, `**/utils/**`)
- `LongParameterList`: 6 for functions, 7 for constructors (excludes `**/ui/**`)
- `ForbiddenComment`: No `TODO:`, `FIXME:`, or `STOPSHIP:` comments (excludes `**/ui/**`)
- `MagicNumber`: Active (excludes `**/ui/**`)
- `FunctionNaming`: camelCase enforced (excludes `**/ui/**`, `**/utils/**`, `**/troubleshoot/**`)
- `WildcardImport`: Forbidden (except `java.util.*`)
- `ReturnCount`: Max 2 returns per function

## Configuration

- `local.properties` — Contains `BASE_URL` and other secrets (not committed)
- `keystore.properties` — Release signing config (not committed)
- Secrets are injected via `com.google.android.libraries.mapsplatform.secrets-gradle-plugin`
