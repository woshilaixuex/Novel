# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests
./gradlew lint                   # Run Android Lint
./gradlew clean                  # Clean build outputs
```

**Build config:** minSdk 26, targetSdk 34, compileSdk 34, JVM target 17, Kotlin 1.9.22, KSP for annotation processing.

## Architecture

MVVM with MVI-inspired intent-based state management.

**Data flow:**
```
DTO (Network/Mock) → Domain Model → VO (View Object) → Fragment/UI
                           ↕
                       Mapper layer
```

**Layers:**
- `model/dto/` — Network data transfer objects
- `model/domain/` — Business models (`Book`, `Chapter`)
- `model/vo/` — View objects for UI (`BookVo`, `ChapterVo`, `ReadingPageVo`)
- `model/mapper/` — Conversions between layers (`BookMapper`, `ChapterMapper`)
- `view/` — Fragments + ViewModels
- `database/` — Room DAOs and databases
- `network/` — Retrofit services + Jsoup web scraping
- `di/` — Hilt modules
- `utils/` — Utilities and mock data generators

## Reading Page System

There are two reading implementations — the legacy `novelPage/` (Fragment + ViewPager2 + `PageFragment`) and the active `newNovelPage/` (custom `PageTurnView`). Current development is in `newNovelPage/`.

**`newNovelPage/` data flow:**
1. `ReadFragment` sends intents to `ReadViewModel` via `sendIntent(ReadIntent.*)`
2. `ReadViewModel` calls `BookDataMaker` (mock) or network, maps DTO→VO
3. `ReadFragment` paginates text using `CustomNovelText.getPages()` based on screen height
4. Pages are handed to `ReadPageProvider`, which binds them to `PageTurnView`
5. `PageTurnView` renders with one of three animations: `COVER`, `SIMULATION`, `TRANSLATION`

**Intent pattern:**
```kotlin
// Define intents as sealed class in ReadViewUiState.kt
sealed class ReadIntent {
    object ShowOrHideBar : ReadIntent()
    data class LoadChapterWithId(val chapterId: Int) : ReadIntent()
    data class TurnPage(val pageIndex: Int) : ReadIntent()
}

// Dispatch from Fragment
viewModel.sendIntent(ReadIntent.LoadChapterWithId(id))

// Collect state in Fragment
viewModel.readState.collect { state -> updateUI(state) }
```

## State Management Pattern

ViewModels expose `StateFlow` for UI state and `SharedFlow`/`StateFlow` for one-shot events. State is updated via data class `.copy()`. Use `viewModelScope.launch` for coroutine operations.

## Dependency Injection

Hilt is used throughout. `AppModule` provides singleton instances of `OkHttpClient`, `Retrofit`, `SearchService`, and all Room DAOs. Fragments and ViewModels use `@AndroidEntryPoint` / `@HiltViewModel`.

## Navigation

Android Navigation Component with SafeArgs. `BookBrief` objects are passed between screens as serialized JSON arguments (Kotlinx Serialization + `@Parcelize`).

## Network / Scraping

`SearchService` (Retrofit) hits novel websites. `WebCrawler` / `SimpleWebCrawler` use Jsoup for HTML parsing. The OkHttp client in `AppModule` injects Chinese novel site cookies/headers.

## Current Development State

The reading page is mid-migration from `novelPage/` to `newNovelPage/`. `BookDataMaker` in `utils/` generates mock data — real network/DB integration is pending. The modified files on `feature/v0.0.1` are:
- `customView/novel/ReadPageProvider.kt`
- `newNovelPage/ReadFragment.kt`
- `newNovelPage/ReadViewModel.kt`
- `newNovelPage/ReadViewUiState.kt`

## Key Notes

- Project UI strings and code comments are in Chinese.
- `ViewPager2` in the legacy reader uses `offscreenPageLimit = 3`.
- Gradle repositories are mirrored to Aliyun/Tencent/Huawei Cloud (Chinese mirrors).
- `kotlinx-serialization` version conflicts are resolved via `exclude` in `build.gradle.kts`.
