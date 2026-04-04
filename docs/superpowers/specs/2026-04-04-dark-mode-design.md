# Dark Mode Support — Design Spec

## Overview

Add dark mode to all Compose UI screens. Theme follows system setting by default, with a manual override (Light / Dark / System) in Settings. Code editor activity, Glance widget, and network monitor are out of scope.

## Approach

Hybrid: use `MaterialTheme.colorScheme` tokens for standard roles (surface, background, text) and `ColorScheme` extension properties for app-specific colors (gradients, status indicators, pie chart slices). This extends the existing pattern already used for `easyCategory`/`mediumCategory`/`hardCategory`.

## Theme Preference Storage

- New `"theme_mode"` string preference in `UserDatastore` with values `"system"`, `"light"`, `"dark"`. Defaults to `"system"`.
- `getThemeMode(): Flow<String>` and `saveThemeMode(mode: String)` methods added to `UserDatastore`.
- `MainActivity` collects the theme flow and passes the resolved `darkTheme: Boolean` to `LeetcodePlusTheme`.
- When `"system"`: uses `isSystemInDarkTheme()`. When `"light"` or `"dark"`: forces the value.

## XML Theme Changes

All `themes.xml` variants (`values/`, `values-v31/`, `values-v35/`) change base theme from `android:Theme.Material.Light.NoActionBar` to `android:Theme.Material.DayNight.NoActionBar`.

## Color System

### MaterialTheme token replacements

| Hardcoded color | Replacement |
|---|---|
| `Color.White` backgrounds/cards | `MaterialTheme.colorScheme.surface` |
| `Color(0xFFABDEF5).copy(alpha = 0.1f)` top bar containers | `MaterialTheme.colorScheme.surfaceVariant` |
| `Color.White` in TextFieldDefaults (search bars) | `MaterialTheme.colorScheme.surface` |
| `Color.Gray` text | `MaterialTheme.colorScheme.onSurfaceVariant` |
| `Color.LightGray` | `MaterialTheme.colorScheme.outlineVariant` |

### New ColorScheme extension properties in Color.kt

| Property | Light | Dark |
|---|---|---|
| `profileGradientStart` | `0xFF4CAF50` | `0xFF2E7D32` |
| `profileGradientEnd` | `Color.LightGray` | `0xFF424242` |
| `allProblemsGradientStart` | `0xFF6dd5ed` | `0xFF1A6B7A` |
| `allProblemsGradientEnd` | `0xFF2193b0` | `0xFF0D3D4A` |
| `goalCompletedBackground` | `0xFFE8F5E9` | `0xFF1B3A1E` |
| `goalInProgressBackground` | `0xFFFFF8E1` | `0xFF3A3520` |
| `goalDefaultBackground` | `0xFFF5F5F5` | `0xFF2C2C2C` |
| `goalCompletedText` | `0xFF4CAF50` | `0xFF81C784` |
| `goalInProgressText` | `0xFFFFA000` | `0xFFFFCC02` |
| `goalDefaultText` | `0xFF757575` | `0xFFBDBDBD` |
| `searchFieldBackground` | `0xFFE3F2FD` | `0xFF1A2733` |
| `targetDifficultyEasy` | `0xFFE0F7FA` alpha 0.4 | `0xFF004D40` alpha 0.4 |
| `targetDifficultyMedium` | `0xFFFFF9C4` alpha 0.4 | `0xFF5D4037` alpha 0.4 |
| `targetDifficultyHard` | `0xFFFFCDD2` alpha 0.4 | `0xFFB71C1C` alpha 0.4 |

### Unchanged colors

- `Color.White` text/icons on gradient backgrounds (always white for contrast)
- `Color.Red` logout icon
- `Color.Yellow` star rating
- Network monitor colors (debug-only, out of scope)
- `DoneIcon.kt` `Color.Black` fill

## Settings UI

New "Appearance" section card in `SettingsScreen`, placed between "Account" and "Notifications". Contains a "Theme" row with a three-option segmented button or radio group: Light / Dark / System. Current selection is highlighted. `SettingsViewModel` reads/writes the preference via `UserDatastore`.

## Files Changed

| File | Change |
|---|---|
| `ui/theme/Color.kt` | Add extension properties for app-specific dark colors |
| `ui/theme/Theme.kt` | Accept theme mode, resolve to darkTheme boolean |
| `data/datastore/UserDatastore.kt` | Add theme preference read/write |
| `ui/MainActivity.kt` | Collect theme preference, pass to theme |
| `ui/screens/settings/SettingsScreen.kt` | Add appearance section + toggle |
| `ui/screens/settings/SettingsViewModel.kt` | Read/write theme preference |
| `ui/screens/home/HomeScreen.kt` | Replace hardcoded colors |
| `ui/screens/allproblems/AllProblemsScreen.kt` | Replace hardcoded colors |
| `ui/screens/targetset/SetWeeklyTargetScreen.kt` | Replace hardcoded colors |
| `ui/screens/targetstatus/GoalProgressScreen.kt` | Replace hardcoded colors |
| `ui/screens/contest/details/ContestDetailScreen.kt` | Replace hardcoded colors |
| `ui/screens/solutions/VideoSolutionsScreen.kt` | Replace hardcoded colors |
| `ui/screens/webview/CommonWebViewScreen.kt` | Replace hardcoded colors |
| `ui/screens/problem/details/ProblemDetailsScreen.kt` | Replace hardcoded colors |
| `ui/screens/MainScreen.kt` | Replace hardcoded colors |
| `ui/screens/settings/composables/DailyProblemWidgetPlaceholder.kt` | Replace hardcoded colors |
| `ui/dialogs/WeeklyGoalResetDialog.kt` | Replace hardcoded colors |
| `res/values/themes.xml` | DayNight base theme |
| `res/values-v31/themes.xml` | DayNight base theme |
| `res/values-v35/themes.xml` | DayNight base theme |

## Out of Scope

- `CodeEditorSubmitActivity` (Views-based code editor)
- `Glance` widget
- `NetworkMonitorActivity` (debug-only)
