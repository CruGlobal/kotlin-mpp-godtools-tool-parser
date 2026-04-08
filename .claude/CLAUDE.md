# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`godtools-shared` is a **Kotlin Multiplatform (KMP)** library that parses and renders interactive GodTools content (lessons, tracts, etc.) across Android, iOS, and JavaScript/Web platforms.

## Build & Development Commands

```bash
# Build all targets
./gradlew assemble

# Run Android unit tests with snapshot verification and code coverage
./gradlew test verifyPaparazzi koverXmlReportRelease

# Run a single test class (Android)
./gradlew :module:parser:testDebugUnitTest --tests "org.cru.godtools.shared.parser.FooTest"

# Run iOS unit tests
./gradlew iosSimulatorArm64Test

# Run JavaScript unit tests
./gradlew jsTest

# Code style checks
./gradlew :build-logic:ktlintCheck ktlintCheck
./gradlew ktlintFormat                   # Auto-fix code style issues

# Android lint
./gradlew lint

# Snapshot testing (Android) — recording only happens in CI
./gradlew verifyPaparazzi                # Verify snapshots
# To record new/updated snapshots: trigger the "Record Snapshots" workflow_dispatch
# in GitHub Actions on the target branch — it commits the updated snapshot images back to the branch

# Generate iOS CocoaPods podspec
./gradlew podspec
```

## Module Structure

```
module/
  analytics/          # Analytics event tracking
  common/             # Shared utilities (colormath, logging)
  interop/            # iOS-specific interop layer (iOS-only)
  parser-base/        # Base types shared by parser modules
  parser-expressions/ # ANTLR-based expression grammar & evaluation
  parser/             # XML parser for tool manifests
  renderer-state/     # Reactive state management (Circuit, Coroutines/Flow)
  renderer/           # Jetpack Compose UI components
  user-activity/      # User engagement tracking
build-logic/          # Gradle convention plugins applied to all modules
publishing/
  npm/                # JavaScript npm package config
  spm/                # Swift Package Manager config
```

**Dependency direction:** `parser-expressions` → `parser-base` → `parser` → `renderer-state` → `renderer`

## Architecture

### Platforms & Targets
Each module supports `androidTarget`, `iosArm64`, `iosSimulatorArm64`, and `js` via the `godtools-shared.module-conventions` convention plugin in `build-logic/`.

### Parsing Pipeline
- `ManifestParser` uses a factory-injected `XmlPullParser` (Android/iOS/JS implementations differ)
- Parsing is suspendable; results are sealed: `ParserResult.Data` or `ParserResult.Error` (Corrupted, NotFound)
- `@KustomExport` annotations on key types generate JS/TypeScript definitions

### State Management
- `renderer-state` holds a `State` class implementing `ExpressionContext` for variable evaluation
- Reactive events flow via Kotlin `MutableSharedFlow`: `OpenUrl`, `AnalyticsEvent`, `SubmitForm`, `OpenTip`
- Tracks accordion state, form field values, and variables

### Rendering
- Compose components in `renderer/content/` consume state from `renderer-state`
- Component types: `RenderImage`, `RenderVideo`, `RenderInput`, `RenderTabs`, `RenderAnimation`, etc.

### Expression Language
- ANTLR grammar in `parser-expressions` compiles to all platforms
- Evaluates dynamic conditions and variable substitutions at runtime

## Key Technologies

| Technology | Purpose |
|---|---|
| Kotlin Multiplatform 2.3.x | Cross-platform code sharing |
| Jetpack Compose 1.10.x | UI rendering |
| Circuit 0.33.x | Reactive UI state management |
| ANTLR-Kotlin | Expression grammar parsing |
| Kotlin Coroutines/Flow | Async and reactive streams |
| KustomExport | Kotlin → JS/TypeScript export |
| Kover | Code coverage |
| Paparazzi | Compose snapshot testing |
| Ktlint | Code style enforcement |

## Versioning & Publishing

- Version is defined in `gradle.properties` (`godtools.version`)
- Appends `-SNAPSHOT` unless `releaseBuild=true`
- PR builds get a `PR{number}` suffix
- Publishes to: JFrog Artifactory (Maven), CocoaPods (`GodToolsToolParser`), npm (`@godtools/tool-parser`)
- After a release, the patch version is auto-incremented and committed

## Gradle Configuration

All modules apply the `godtools-shared.module-conventions` convention plugin which sets up:
- Multiplatform targets (Android + iOS + JS)
- Kover code coverage
- Maven publishing
- Ktlint

Dependency versions are centralized in `gradle/libs.versions.toml`. Custom Maven repos include CruGlobal JFrog (for custom ANTLR-Kotlin and material-color-utilities builds), JitPack, and Deezer KustomExport.

## Code Style

- Max line length: 120 characters
- ktlint with `android_studio` code style
- 4-space indent for Kotlin files
- Trailing commas allowed but not enforced
- Composable functions exempt from standard function naming rules
