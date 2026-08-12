# RenderTractPage Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a `RenderTractPage` composable to `module/renderer` rendering a single Tract page with full interactive parity with godtools-android's view-based `PageController`/`PageContentLayout`.

**Architecture:** A custom `Layout` (`TractPageLayout`) ports `PageContentLayout`'s measure/placement semantics, reading card anchor geometry via Compose alignment lines published by a new `RenderTractCard` chrome composable. A hoisted saveable `TractPageState` owns active-card/hidden-card state. Animations run through per-child `Animatable`s driven by a coordinator; gestures arrive via `NestedScrollConnection` + a raw pointer-input fling detector. Modals surface as a new `State.Event.OpenModal`; page-scoped interactions surface via a sealed `TractPageEvent`.

**Tech Stack:** Kotlin Multiplatform (android/ios/js), Jetpack Compose 1.11.x, compose resources, kotlin.test + `runComposeUiTest` + Turbine (commonTest), Paparazzi (androidHostTest).

**Spec:** `docs/superpowers/specs/2026-08-12-render-tract-page-design.md`

## Global Constraints

- Max line length 120; ktlint `android_studio` style; 4-space indent. Check with `./gradlew ktlintCheck`, fix with `./gradlew ktlintFormat`.
- No trailing comma after a multiline `Modifier` argument that is a call's last parameter.
- Do NOT add `@KustomExport` to any new type.
- Run `./gradlew assemble` before EVERY commit (catches iOS/JS compile failures that Android tests miss).
- All commonMain composables/tests must compile for `androidTarget`, `iosArm64`, `iosSimulatorArm64`, and `js` — no platform-specific APIs in commonMain.
- Behavior tests: commonTest, extend `BaseRendererTest`, annotate `@RunOnAndroidWith(AndroidJUnit4::class)` + `@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)`, run via `./gradlew :module:renderer:testAndroidHostTest --tests "<class>"`.
- Test style: describe expected behavior in the assertion's message argument (`assertEquals(expected, actual, "…")`), never in a comment above the assertion. Compose UI assertions without a message parameter stay bare.
- Test naming: segmented `Category - target - behavior` backtick names (e.g. `Action - label - tap toggles card`, `UI - arrow - not rendered on last page`, `Event - onDismiss - dismissListener content event`); categories: Action, Event, UI, and analogous (State, Analytics, Layout). No `&` or `,` in test names (they break Kotlin/Native iOS compiles).
- Snapshot tests: androidHostTest, extend `BasePaparazziTest`. Recording is CI-only — do NOT attempt to record locally; the final task triggers the CI workflow via the `record-screenshots` skill.
- Commit messages: plain imperative subject (repo style), ending with `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>`.
- New public API in this plan: `RenderTractPage`, `TractPageEvent`, `TractPageState`, `rememberTractPageState`, `State.Event.OpenModal`. Everything else is `internal`.

---

### Task 1: `State.Event.OpenModal`

**Files:**
- Modify: `module/renderer-state/src/commonMain/kotlin/org/cru/godtools/shared/renderer/state/State.kt` (Events region, ~line 67)
- Test: `module/renderer-state/src/commonTest/kotlin/org/cru/godtools/shared/renderer/state/StateTest.kt`

**Interfaces:**
- Consumes: existing `State.triggerEvent(event: Event)` and `State.events: SharedFlow<Event>`.
- Produces: `data class OpenModal(val pageId: String, val modalId: String) : State.Event` — Task 9 triggers it; hosts collect it from `state.events`.

- [ ] **Step 1: Write the failing test**

Add to `StateTest.kt` (match the file's existing imports/style; it already uses Turbine — check existing tests in the file and mirror their pattern):

```kotlin
@Test
fun `Event - OpenModal`() = runTest {
    val state = State()
    state.events.test {
        state.triggerEvent(State.Event.OpenModal(pageId = "page0", modalId = "page0-modal-0"))
        assertEquals(State.Event.OpenModal(pageId = "page0", modalId = "page0-modal-0"), awaitItem())
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :module:renderer-state:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.state.StateTest"`
Expected: compile FAIL — `OpenModal` unresolved.

- [ ] **Step 3: Write minimal implementation**

In `State.kt`, inside `sealed class Event`, after `data class OpenTip`:

```kotlin
data class OpenModal(val pageId: String, val modalId: String) : Event()
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :module:renderer-state:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.state.StateTest"`
Expected: PASS

- [ ] **Step 5: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer-state
git commit -m "Add State.Event.OpenModal event"
```

---

### Task 2: `TractPageState` + saver

**Files:**
- Create: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/TractPageState.kt`
- Test: `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/tract/TractPageStateTest.kt`

**Interfaces:**
- Consumes: `TractPage`, `TractPage.Card` from parser (test constructors: `TractPage(cards = { page -> listOf(TractPage.Card(page, 0, ...)) })`, `Card(page, position, isHidden = ...)`).
- Produces (used by Tasks 7–9):
  - `@Stable class TractPageState(page: TractPage)` with `var page: TractPage` (private set, snapshot state); `val activeCard: TractPage.Card?` (private set); `val activeCardPosition: Int` (−1 = hero); `val visibleCards: List<TractPage.Card>`; `var isBounceFirstCard: Boolean`; `fun navigateToCard(card: TractPage.Card?)`; `fun updatePage(page: TractPage)`; `internal fun nextCard(): Boolean`; `internal fun previousCard(): Boolean`; `internal fun dismissActiveCard()`; `companion object { fun Saver(page: TractPage): Saver<TractPageState, *> }`
  - `@Composable fun rememberTractPageState(page: TractPage): TractPageState`
  - **Language-switcher support:** hosts can toggle between two parallel translations of the same logical
    page; a different `TractPage` instance arrives for the same position. The active card is tracked as
    `activeCardId` (the single `mutableStateOf` source of truth) with `activeCard` DERIVED from it, so
    the "activeCard belongs to the current page" invariant holds by construction and `updatePage` is a
    trivial page swap (`"${page.id}-card-$position"` ids line up across translations; while unmatched,
    `activeCard` derives to null/hero). `enabledHiddenCards` ids carry over so `visibleCards` re-derives.
    `rememberTractPageState` must NOT key `rememberSaveable` on the page — it remembers once and applies
    `updatePage(page)` on recomposition, mirroring `rememberLessonPagerState`'s `updateManifest` pattern.
    This supersedes the `activeCard` property shown in the implementation block above:

```kotlin
@Composable
fun rememberTractPageState(page: TractPage) =
    rememberSaveable(saver = TractPageState.Saver(page)) { TractPageState(page) }
        .apply { updatePage(page) }
```

```kotlin
private var activeCardId: String? by mutableStateOf(activeCardId)
val activeCard: TractPage.Card? by derivedStateOf { page.cards.firstOrNull { it.id == activeCardId } }

fun navigateToCard(card: TractPage.Card?) {
    require(card == null || card.page == page) { "card must belong to this state's page" }
    if (card?.isHidden == true) enabledHiddenCards += card.id
    activeCardId = card?.id
    hideInactiveHiddenCards()
}

fun updatePage(page: TractPage) {
    this.page = page
    if (activeCardId != null && page.cards.none { it.id == activeCardId }) navigateToCard(null)
}
```

  (`page` is snapshot-backed — `var page: TractPage by mutableStateOf(page); private set` — so both
  derivations re-derive on page swaps; the `Saver` persists `activeCardId` directly. When the new page
  has no card matching the active id, `updatePage` resets to the hero permanently via
  `navigateToCard(null)` — routing through `navigateToCard` keeps the enabled-hidden-cards invariant
  intact and re-hides revealed hidden cards; never null `activeCardId` directly. Tests: preservation
  across structurally-equivalent pages including a revealed hidden card staying revealed, and the
  permanent reset — updatePage to a missing-id page → hero; updatePage back → still hero and the
  hidden card re-hidden — with expected behavior described in assertion message arguments.)

- [ ] **Step 1: Write the failing tests**

```kotlin
package org.cru.godtools.shared.renderer.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

class TractPageStateTest {
    private val page = TractPage(
        cards = { page ->
            listOf(
                TractPage.Card(page, 0),
                TractPage.Card(page, 1, isHidden = true),
                TractPage.Card(page, 2),
            )
        },
    )

    @Test
    fun `visibleCards - excludes hidden cards by default`() {
        val state = TractPageState(page)
        assertEquals(listOf(page.cards[0], page.cards[2]), state.visibleCards)
    }

    @Test
    fun `navigateToCard - enables hidden card`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])
        assertEquals(page.cards[1], state.activeCard)
        assertEquals(page.cards, state.visibleCards)
        assertEquals(1, state.activeCardPosition)
    }

    @Test
    fun `navigateToCard - deactivating a hidden card re-hides it`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])
        state.navigateToCard(page.cards[0])
        assertEquals(page.cards[0], state.activeCard)
        assertEquals(listOf(page.cards[0], page.cards[2]), state.visibleCards)
    }

    @Test
    fun `nextCard & previousCard - navigate within visible cards`() {
        val state = TractPageState(page)
        assertTrue(state.nextCard())
        assertEquals(page.cards[0], state.activeCard)
        assertTrue(state.nextCard())
        assertEquals(page.cards[2], state.activeCard)
        assertFalse(state.nextCard())
        assertTrue(state.previousCard())
        assertEquals(page.cards[0], state.activeCard)
        assertTrue(state.previousCard())
        assertNull(state.activeCard)
        assertEquals(-1, state.activeCardPosition)
        assertFalse(state.previousCard())
    }

    @Test
    fun `dismissActiveCard - returns to hero`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[0])
        state.dismissActiveCard()
        assertNull(state.activeCard)
    }

    @Test
    fun `Saver - round trip preserves activeCard and enabled hidden cards`() {
        val state = TractPageState(page)
        state.navigateToCard(page.cards[1])

        val saver = TractPageState.Saver(page)
        val saved = with(saver) { TestSaverScope.save(state) }!!
        val restored = saver.restore(saved)!!

        assertEquals(page.cards[1], restored.activeCard)
        assertEquals(page.cards, restored.visibleCards)
    }

    private object TestSaverScope : androidx.compose.runtime.saveable.SaverScope {
        override fun canBeSaved(value: Any) = true
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractPageStateTest"`
Expected: compile FAIL — `TractPageState` unresolved.

- [ ] **Step 3: Write the implementation**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@Composable
fun rememberTractPageState(page: TractPage) =
    rememberSaveable(page, saver = TractPageState.Saver(page)) { TractPageState(page) }

@Stable
class TractPageState internal constructor(
    val page: TractPage,
    activeCardId: String?,
    enabledHiddenCards: Collection<String>,
) {
    constructor(page: TractPage) : this(page, activeCardId = null, enabledHiddenCards = emptySet())

    internal val enabledHiddenCards = mutableStateSetOf(*enabledHiddenCards.toTypedArray())
    val visibleCards by derivedStateOf {
        page.cards.filter { !it.isHidden || it.id in this.enabledHiddenCards }
    }

    var activeCard: TractPage.Card? by mutableStateOf(page.cards.firstOrNull { it.id == activeCardId })
        private set
    val activeCardPosition get() = visibleCards.indexOf(activeCard)

    /** Host-settable: enables the periodic first-card bounce hint while no card is active. */
    var isBounceFirstCard by mutableStateOf(false)

    fun navigateToCard(card: TractPage.Card?) {
        require(card == null || card.page == page) { "card must belong to this state's page" }
        if (card?.isHidden == true) enabledHiddenCards += card.id
        activeCard = card
        hideInactiveHiddenCards()
    }

    internal fun nextCard(): Boolean {
        val next = visibleCards.getOrNull(activeCardPosition + 1) ?: return false
        navigateToCard(next)
        return true
    }

    internal fun previousCard(): Boolean {
        if (activeCardPosition < 0) return false
        navigateToCard(visibleCards.getOrNull(activeCardPosition - 1))
        return true
    }

    internal fun dismissActiveCard() = navigateToCard(null)

    private fun hideInactiveHiddenCards() {
        enabledHiddenCards.removeAll { it != activeCard?.id }
    }

    companion object {
        fun Saver(page: TractPage) = listSaver<TractPageState, Any?>(
            save = { listOf(it.activeCard?.id, ArrayList(it.enabledHiddenCards)) },
            restore = {
                @Suppress("UNCHECKED_CAST")
                TractPageState(page, activeCardId = it[0] as String?, enabledHiddenCards = it[1] as List<String>)
            },
        )
    }
}
```

Note: known fidelity difference vs Android (accepted by spec review): a revealed hidden card is removed from `visibleCards` immediately on deactivation, not after the deactivation animation completes.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractPageStateTest"`
Expected: PASS

- [ ] **Step 5: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer
git commit -m "Add TractPageState for tract page card state"
```

---

### Task 3: `ProvideResumedLifecycleOwner` util

**Files:**
- Create: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/util/ProvideResumedLifecycleOwner.kt`
- Modify: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/util/ProvideCurrentPageLifecycleOwner.kt` (delegate to the new util)
- Test: `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/util/ProvideResumedLifecycleOwnerTest.kt`

**Interfaces:**
- Consumes: `androidx.lifecycle.compose.rememberLifecycleOwner`, `LocalLifecycleOwner`.
- Produces: `@Composable internal fun ProvideResumedLifecycleOwner(resumed: Boolean, content: @Composable () -> Unit)` — caps `content`'s lifecycle at STARTED unless `resumed`. Used by Task 9 for hero/card slots.

- [ ] **Step 1: Write the failing test**

```kotlin
package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class ProvideResumedLifecycleOwnerTest : BaseRendererTest() {
    @Test
    fun `content is only resumed while resumed=true`() = runComposeUiTest {
        var resumed by mutableStateOf(false)
        var resumeCount = 0
        var pauseCount = 0

        setContent {
            ProvideTestCompositionLocals {
                ProvideResumedLifecycleOwner(resumed = resumed) {
                    LifecycleResumeEffect(Unit) {
                        resumeCount++
                        onPauseOrDispose { pauseCount++ }
                    }
                }
            }
        }

        assertEquals(0, resumeCount)

        resumed = true
        waitForIdle()
        assertEquals(1, resumeCount)
        assertEquals(0, pauseCount)

        resumed = false
        waitForIdle()
        assertEquals(1, resumeCount)
        assertEquals(1, pauseCount)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.util.ProvideResumedLifecycleOwnerTest"`
Expected: compile FAIL — `ProvideResumedLifecycleOwner` unresolved.

- [ ] **Step 3: Write the implementation**

```kotlin
package org.cru.godtools.shared.renderer.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner

/**
 * Provide a [LocalLifecycleOwner] to [content] that is capped at STARTED unless [resumed] is true, so
 * lifecycle-aware effects within [content] only run while it is the "current" child.
 */
@Composable
internal fun ProvideResumedLifecycleOwner(resumed: Boolean, content: @Composable () -> Unit) {
    val lifecycleOwner = rememberLifecycleOwner(
        maxLifecycle = if (resumed) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
    )
    CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner, content = content)
}
```

Refactor `ProvideCurrentPageLifecycleOwner.kt` to delegate (keep its KDoc):

```kotlin
@Composable
internal fun ProvideCurrentPageLifecycleOwner(pagerState: PagerState, page: Int, content: @Composable () -> Unit) {
    val isSettledPage by remember(pagerState, page) { derivedStateOf { page == pagerState.settledPage } }
    ProvideResumedLifecycleOwner(resumed = isSettledPage, content = content)
}
```

- [ ] **Step 4: Run tests to verify they pass (including existing pager lifecycle consumers)**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.util.ProvideResumedLifecycleOwnerTest" --tests "org.cru.godtools.shared.renderer.lesson.RenderLessonTest" --tests "org.cru.godtools.shared.renderer.tips.RenderTipTest"`
Expected: PASS

- [ ] **Step 5: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer
git commit -m "Extract ProvideResumedLifecycleOwner lifecycle helper"
```

---

### Task 4: `RenderTractCallToAction`

**Files:**
- Create: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractCallToAction.kt`
- Create: `module/renderer/src/commonMain/composeResources/drawable/ic_call_to_action.xml`
- Modify: `module/renderer/src/commonMain/composeResources/values/strings_renderer.xml`
- Test: `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractCallToActionTest.kt`

**Interfaces:**
- Consumes: `CallToAction?.controlColor` extension (`org.cru.godtools.shared.tool.parser.model.tract.controlColor`), `RenderTextNode`, `ToolTheme.ContentTextStyle`, `page.isLastPage`.
- Produces: `@Composable internal fun RenderTractCallToAction(page: TractPage, onNextPage: () -> Unit, modifier: Modifier = Modifier)`. Tests resolve the arrow by its contentDescription ("Next Page") — no test tag. Used by Task 9 as the layout's call-to-action slot.

- [ ] **Step 1: Add resources**

Append to the end of `strings_renderer.xml` (before `</resources>`):

```xml

    <!-- Tract Strings -->
    <eat-comment />
    <string name="tract_accessibility_action_next_page">Next Page</string>
```

Create `drawable/ic_call_to_action.xml` (ported from godtools-android; `@color/tintable` replaced with a literal since Compose applies the tint):

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:autoMirrored="true"
    android:viewportHeight="24.0"
    android:viewportWidth="24.0">
    <path
        android:fillColor="#FF000000"
        android:pathData="M19.871,11.084L11.352,2.565C10.994,2.207 10.994,1.627 11.352,1.269C11.71,0.91 12.29,0.91 12.648,1.269L22.731,11.352C23.09,11.71 23.09,12.29 22.731,12.649C22.731,12.649 12.648,22.732 12.648,22.732C12.469,22.91 12.234,23.001 12,23.001C11.765,23.001 11.53,22.91 11.351,22.732C10.993,22.374 10.993,21.793 11.351,21.435L19.87,12.917L1.917,12.917C1.411,12.917 1,12.507 1,12.001C1,11.494 1.411,11.084 1.917,11.084L19.871,11.084Z"
        android:strokeColor="#00000000"
        android:strokeWidth="1" />
</vector>
```

- [ ] **Step 2: Write the failing tests**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.CallToAction
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class RenderTractCallToActionTest : BaseRendererTest() {
    private var nextPageCalled = 0

    @Test
    fun `renders label and arrow, arrow advances page`() = runComposeUiTest {
        val manifest = Manifest(
            pages = {
                listOf(
                    TractPage(
                        it,
                        callToAction = { p -> CallToAction(p, label = { c -> Text(c, text = "Keep going") }) },
                    ),
                    TractPage(it),
                )
            },
        )
        val page = manifest.pages.first() as TractPage

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCallToAction(page, onNextPage = { nextPageCalled++ })
            }
        }

        onNodeWithText("Keep going").assertExists()
        onNodeWithContentDescription("Next Page").performClick()
        assertEquals(1, nextPageCalled)
    }

    @Test
    fun `arrow is not rendered on the last page`() = runComposeUiTest {
        val manifest = Manifest(pages = { listOf(TractPage(it), TractPage(it)) })

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCallToAction(manifest.pages.last() as TractPage, onNextPage = { nextPageCalled++ })
            }
        }

        onNodeWithContentDescription("Next Page").assertDoesNotExist()
    }
}
```

(If the `Manifest`/`Text` test-constructor parameter names differ, check their `@RestrictTo(TESTS)` constructors in `module/parser` and adjust — `Text` is `Text(parent, text = ...)`, `Manifest(pages = { ... })` mirrors existing renderer tests.)

- [ ] **Step 3: Run tests to verify they fail**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.RenderTractCallToActionTest"`
Expected: compile FAIL — `RenderTractCallToAction` unresolved.

- [ ] **Step 4: Write the implementation**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.ic_call_to_action
import org.cru.godtools.shared.renderer.generated.resources.tract_accessibility_action_next_page
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.controlColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val CallToActionVerticalPadding = 8.dp
private val CallToActionLabelStartMargin = 32.dp
private val CallToActionArrowEndMargin = 8.dp
private val CallToActionArrowSize = 40.dp
private val CallToActionArrowPadding = 8.dp

@Composable
internal fun RenderTractCallToAction(page: TractPage, onNextPage: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = CallToActionVerticalPadding)
    ) {
        ProvideTextStyle(ToolTheme.ContentTextStyle) {
            page.callToAction.label?.let {
                RenderTextNode(
                    it,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = CallToActionLabelStartMargin)
                )
            } ?: Spacer(Modifier.weight(1f))
        }
        if (!page.isLastPage) {
            Icon(
                painterResource(Res.drawable.ic_call_to_action),
                contentDescription = stringResource(Res.string.tract_accessibility_action_next_page),
                tint = page.callToAction.controlColor.toComposeColor(),
                modifier = Modifier
                    .padding(end = CallToActionArrowEndMargin)
                    .size(CallToActionArrowSize)
                    .clickable(onClick = onNextPage)
                    .padding(CallToActionArrowPadding)
            )
        }
    }
}
```

(Add the missing `Spacer` import: `androidx.compose.foundation.layout.Spacer`.)

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.RenderTractCallToActionTest"`
Expected: PASS

- [ ] **Step 6: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer
git commit -m "Add RenderTractCallToAction composable"
```

---

### Task 5: `RenderTractCard` chrome with alignment lines

**Files:**
- Create: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractCard.kt`
- Modify: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/ToolTheme.kt` (add `TractCardLabelTextStyle`)
- Modify: `module/renderer/src/commonMain/composeResources/values/strings_renderer.xml` (Tract group)
- Test: `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractCardTest.kt`

**Interfaces:**
- Consumes: `RenderTractCardContent`, `RenderTextNode`, `painterTip`, `Card?.backgroundColor` extension, `State.showTips`, `triggerScreenView`, `triggerAnalyticsEvents`, `ToolAnalyticsScreenNames.forTractPage(page, card)`.
- Produces (used by Tasks 7–9):
  - `@Composable internal fun RenderTractCard(card: TractPage.Card, state: State, onToggleCard: () -> Unit, onPreviousCard: () -> Unit, onNextCard: () -> Unit, modifier: Modifier = Modifier)`
  - Alignment lines: `internal val TractCardPaddingLine: HorizontalAlignmentLine`, `TractCardPeekLine`, `TractCardStackLine` — published on the card's outer placeable at: top of card surface / top of the label text / bottom of the label divider (distances from the child's top edge).
  - Test tags: only `TestTagTractCard = "tract_card"` (card surface — geometry reference, no semantics of its own) and `TestTagTractCardTipIndicator = "tract_card_tip_indicator"` (decorative image, contentDescription deliberately null). All other elements are selected by their existing semantics in tests: label by its text, previous/next by their uppercased localized text (`"PREVIOUS"`/`"NEXT"`), position by its `"1/3"`-style text.

- [ ] **Step 1: Add strings**

In the Tract group of `strings_renderer.xml`:

```xml
    <string name="tract_card_action_previous">Previous</string>
    <string name="tract_card_action_next">Next</string>
    <string name="tract_card_position">%1$d/%2$d</string>
```

Add to `ToolTheme`:

```kotlin
internal val TractCardLabelTextStyle = Typography().titleMedium.copy(
    fontSize = 18.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 22.sp,
)
```

- [ ] **Step 2: Write the failing tests**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.runCurrent
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class RenderTractCardTest : BaseRendererTest() {
    private var toggleCalled = 0
    private var previousCalled = 0
    private var nextCalled = 0

    private fun page(cards: (TractPage) -> List<TractPage.Card>) = TractPage(cards = cards)

    private fun card(page: TractPage) = page.cards.first()

    @Test
    fun `label tap target toggles the card`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0, label = { p -> Text(p, text = "Card 1") })) }
        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(
                    card(page),
                    state = state,
                    onToggleCard = { toggleCalled++ },
                    onPreviousCard = { previousCalled++ },
                    onNextCard = { nextCalled++ },
                )
            }
        }

        onNodeWithText("Card 1").performClick()
        assertEquals(1, toggleCalled)
    }

    @Test
    fun `nav buttons - middle card navigates in both directions`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0), TractPage.Card(it, 1), TractPage.Card(it, 2)) }
        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(
                    page.cards[1],
                    state = state,
                    onToggleCard = { toggleCalled++ },
                    onPreviousCard = { previousCalled++ },
                    onNextCard = { nextCalled++ },
                )
            }
        }

        onNodeWithTag(TestTagTractCardPrevious).performClick()
        onNodeWithTag(TestTagTractCardNext).performClick()
        assertEquals(1, previousCalled)
        assertEquals(1, nextCalled)
    }

    @Test
    fun `nav buttons - disabled on first & last visible card and hidden cards`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0), TractPage.Card(it, 1, isHidden = true)) }

        setContent {
            ProvideTestCompositionLocals {
                RenderTractCard(page.cards[0], state, {}, {}, {})
            }
        }
        onNodeWithTag(TestTagTractCardPrevious).assertIsNotEnabled()
        onNodeWithTag(TestTagTractCardNext).assertIsNotEnabled()
    }

    @Test
    fun `analytics - resume triggers ScreenView for the card`() = runComposeUiTest {
        // build the page through a Manifest so page.position resolves to 0 (screen name "tool-0a")
        val manifest = org.cru.godtools.shared.tool.parser.model.Manifest(
            code = "tool",
            pages = { m -> listOf(TractPage(m, cards = { p -> listOf(TractPage.Card(p, 0)) })) },
        )
        val page = manifest.pages.first() as TractPage

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ScreenView>().test {
            setContent {
                ProvideTestCompositionLocals {
                    RenderTractCard(page.cards.first(), state, {}, {}, {})
                }
            }
            waitForIdle()
            assertEquals("tool-0a", awaitItem().screenName)
        }
    }

    @Test
    fun `alignment lines - published in expected order`() = runComposeUiTest {
        val page = page { listOf(TractPage.Card(it, 0, label = { p -> Text(p, text = "Card 1") })) }
        var padding = -1
        var peek = -1
        var stack = -1

        setContent {
            ProvideTestCompositionLocals {
                Layout(content = { RenderTractCard(card(page), state, {}, {}, {}) }) { measurables, constraints ->
                    val placeable = measurables.single().measure(constraints)
                    padding = placeable[TractCardPaddingLine]
                    peek = placeable[TractCardPeekLine]
                    stack = placeable[TractCardStackLine]
                    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                }
            }
        }

        assertTrue(padding > 0, "padding line should be below the top margin")
        assertTrue(peek > padding, "peek line (label top) should be below the card surface top")
        assertTrue(stack > peek, "stack line (divider bottom) should be below the label top")
    }
}
```

Note on the ScreenView test: mirror how `RenderLessonPageTest` asserts screen views (check that file first and copy its exact Turbine pattern — the sketch above is directional; events are a hot flow so collect before triggering resume if needed by re-parenting the lifecycle).

- [ ] **Step 3: Run tests to verify they fail**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.RenderTractCardTest"`
Expected: compile FAIL.

- [ ] **Step 4: Write the implementation**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.github.ajalt.colormath.extensions.android.composecolor.toComposeColor
import org.cru.godtools.shared.renderer.ToolTheme
import org.cru.godtools.shared.renderer.content.RenderTextNode
import org.cru.godtools.shared.renderer.content.extensions.painterTip
import org.cru.godtools.shared.renderer.content.extensions.triggerAnalyticsEvents
import org.cru.godtools.shared.renderer.generated.resources.Res
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_next
import org.cru.godtools.shared.renderer.generated.resources.tract_card_action_previous
import org.cru.godtools.shared.renderer.generated.resources.tract_card_position
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.util.triggerScreenView
import org.cru.godtools.shared.tool.analytics.ToolAnalyticsScreenNames
import org.cru.godtools.shared.tool.parser.model.AnalyticsEvent
import org.cru.godtools.shared.tool.parser.model.tract.TractPage
import org.cru.godtools.shared.tool.parser.model.tract.backgroundColor
import org.jetbrains.compose.resources.stringResource

// Alignment lines published by RenderTractCard, measured from the card child's top edge.
internal val TractCardPaddingLine = HorizontalAlignmentLine(::minOf)
internal val TractCardPeekLine = HorizontalAlignmentLine(::minOf)
internal val TractCardStackLine = HorizontalAlignmentLine(::minOf)

private val CardMargin = 16.dp
private val CardCornerRadius = 10.dp
private val CardElevation = 6.dp
private val CardLabelVerticalPaddingTop = 16.dp
private val CardLabelVerticalPaddingBottom = 12.dp
private val CardContentMarginHorizontal = 16.dp
private val CardTipIndicatorSize = 24.dp
private val CardNavHeight = 36.dp
private val CardNavMarginHorizontal = 32.dp

internal const val TestTagTractCard = "tract_card"
internal const val TestTagTractCardLabel = "tract_card_label"
internal const val TestTagTractCardTipIndicator = "tract_card_tip_indicator"
internal const val TestTagTractCardPrevious = "tract_card_previous"
internal const val TestTagTractCardNext = "tract_card_next"
internal const val TestTagTractCardPosition = "tract_card_position"

private const val SLOT_HEADER = "header"
private const val SLOT_CONTENT = "content"
private const val SLOT_NAV = "nav"

@Composable
internal fun RenderTractCard(
    card: TractPage.Card,
    state: State,
    onToggleCard: () -> Unit,
    onPreviousCard: () -> Unit,
    onNextCard: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    LifecycleResumeEffect(card, state) {
        state.triggerScreenView(card.manifest, ToolAnalyticsScreenNames.forTractPage(card.page, card))
        val events = card.triggerAnalyticsEvents(AnalyticsEvent.Trigger.VISIBLE, state, coroutineScope)
        onPauseOrDispose { events.forEach { it.cancel() } }
    }

    Surface(
        color = card.backgroundColor.toComposeColor(),
        shape = RoundedCornerShape(CardCornerRadius),
        shadowElevation = CardElevation,
        modifier = modifier
            .padding(CardMargin)
            .testTag(TestTagTractCard)
    ) {
        Layout(
            content = {
                CardHeader(card, state, onToggleCard, Modifier.layoutId(SLOT_HEADER))
                RenderTractCardContent(card, state = state, modifier = Modifier.layoutId(SLOT_CONTENT))
                CardNav(card, onPreviousCard, onNextCard, Modifier.layoutId(SLOT_NAV))
            },
        ) { measurables, constraints ->
            val loose = constraints.copy(minWidth = constraints.maxWidth, minHeight = 0)
            val header = measurables.first { it.layoutId == SLOT_HEADER }.measure(loose)
            val nav = measurables.first { it.layoutId == SLOT_NAV }.measure(loose)
            val content = measurables.first { it.layoutId == SLOT_CONTENT }.measure(
                loose.copy(maxHeight = (constraints.maxHeight - header.height - nav.height).coerceAtLeast(0))
            )

            val height = constraints.maxHeight
            layout(
                constraints.maxWidth,
                height,
                alignmentLines = mapOf(
                    // the top margin padding modifier shifts these to be relative to the card child's top edge
                    TractCardPaddingLine to 0,
                    TractCardPeekLine to CardLabelVerticalPaddingTop.roundToPx(),
                    TractCardStackLine to header.height,
                ),
            ) {
                header.place(0, 0)
                content.place(0, header.height)
                nav.place(0, height - nav.height)
            }
        }
    }
}

@Composable
private fun CardHeader(
    card: TractPage.Card,
    state: State,
    onToggleCard: () -> Unit,
    modifier: Modifier = Modifier,
) = Column(modifier.clickable(onClick = onToggleCard)) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = CardContentMarginHorizontal)
            .padding(top = CardLabelVerticalPaddingTop, bottom = CardLabelVerticalPaddingBottom)
    ) {
        card.label?.let {
            ProvideTextStyle(ToolTheme.TractCardLabelTextStyle) {
                RenderTextNode(
                    it,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(TestTagTractCardLabel)
                )
            }
        }
        val indicatorTip = card.tips.firstOrNull()
            ?: card.page.callToAction.tip.takeIf { card.isLastVisibleCard }
        if (indicatorTip != null && state.showTips.collectAsState().value) {
            // purely visual indicator: tip-type icon only, no completion state, no separate click handling
            Image(
                painterTip(indicatorTip, isComplete = false),
                contentDescription = null,
                modifier = Modifier
                    .size(CardTipIndicatorSize)
                    .testTag(TestTagTractCardTipIndicator)
            )
        }
    }
    HorizontalDivider(thickness = 1.dp, color = card.textColor.toComposeColor())
}

@Composable
private fun CardNav(
    card: TractPage.Card,
    onPreviousCard: () -> Unit,
    onNextCard: () -> Unit,
    modifier: Modifier = Modifier,
) = Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier.fillMaxWidth()) {
    val visiblePosition = card.visiblePosition
    val visibleCards = card.page.visibleCards.size
    // Hidden cards suppress the entire nav row (invisible-style: space reserved, clicks disabled)
    val previousVisible = !card.isHidden && (visiblePosition ?: 0) > 0
    val positionVisible = !card.isHidden
    val nextVisible = !card.isHidden && (visiblePosition ?: visibleCards) + 1 < visibleCards

    Text(
        stringResource(Res.string.tract_card_action_previous).uppercase(),
        textAlign = TextAlign.Start,
        modifier = Modifier
            .weight(1f)
            .height(CardNavHeight)
            .alpha(if (previousVisible) 1f else 0f)
            .clickable(enabled = previousVisible, onClick = onPreviousCard)
            .padding(start = CardNavMarginHorizontal)
            .testTag(TestTagTractCardPrevious)
    )
    Text(
        stringResource(Res.string.tract_card_position, (visiblePosition ?: 0) + 1, visibleCards),
        modifier = Modifier
            .alpha(if (positionVisible) 1f else 0f)
            .testTag(TestTagTractCardPosition)
    )
    Text(
        stringResource(Res.string.tract_card_action_next).uppercase(),
        textAlign = TextAlign.End,
        modifier = Modifier
            .weight(1f)
            .height(CardNavHeight)
            .alpha(if (nextVisible) 1f else 0f)
            .clickable(enabled = nextVisible, onClick = onNextCard)
            .padding(end = CardNavMarginHorizontal)
            .testTag(TestTagTractCardNext)
    )
}
```

Implementation notes:
- The nav Text vertical centering: wrap the text in `Modifier.height(CardNavHeight).wrapContentHeight()` if it renders top-aligned — adjust while making the tests pass.
- `assertIsNotEnabled()` requires the node to have an `enabled=false` clickable — `clickable(enabled = false)` provides the disabled semantics.
- Manifest-locale strings: `stringResource` resolves from the composition locale; `RenderTractPage` (Task 9) wraps everything in `ProvideLayoutDirectionFromLocale`, matching how lesson/tip strings are handled today.

- [ ] **Step 5: Run tests to verify they pass**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.RenderTractCardTest"`
Expected: PASS

- [ ] **Step 6: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer
git commit -m "Add RenderTractCard chrome composable"
```

---

### Task 6: `TractBounceEasing`

**Files:**
- Create: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/TractBounceEasing.kt`
- Test: `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/tract/TractBounceEasingTest.kt`

**Interfaces:**
- Produces (used by Task 8): `internal class TractBounceEasing(bounces: Int = 4, decay: Double = 0.5) : Easing` with `fun totalDuration(firstBounceDuration: Long): Long`. `transform(0f) == 0f`, `transform(1f) == 0f`, peak of first bounce ≈ 1f.

- [ ] **Step 1: Write the failing test**

```kotlin
package org.cru.godtools.shared.renderer.tract

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TractBounceEasingTest {
    @Test
    fun `transform - starts and ends at 0`() {
        val easing = TractBounceEasing()
        assertEquals(0f, easing.transform(0f))
        assertEquals(0f, easing.transform(1f))
    }

    @Test
    fun `transform - first bounce reaches full height, later bounces decay`() {
        val easing = TractBounceEasing()
        val samples = (1..999).map { easing.transform(it / 1000f) }
        val peak = samples.max()
        assertTrue(peak > 0.99f, "first bounce should reach ~1.0, was $peak")
        // after the first bounce completes, no sample should exceed the decayed height (0.5) + epsilon
        val firstBounceEnd = (1 / easing.totalTime).toFloat()
        val laterPeak = (1..999).map { it / 1000f }.filter { it > firstBounceEnd }
            .maxOf { easing.transform(it) }
        assertTrue(laterPeak <= 0.51f, "later bounces should decay to <= 0.5, was $laterPeak")
    }

    @Test
    fun `totalDuration - scales first bounce duration by total time`() {
        val easing = TractBounceEasing()
        assertTrue(easing.totalDuration(400L) > 400L)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractBounceEasingTest"`
Expected: compile FAIL.

- [ ] **Step 3: Write the implementation** (port of godtools-android `BounceInterpolator`)

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.animation.core.Easing
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Easing that bounces [bounces] times, each bounce's height decaying by [decay].
 * Output is the bounce height fraction (0 = rest position, 1 = full bounce height);
 * it starts and ends at 0. Port of godtools-android's BounceInterpolator.
 */
internal class TractBounceEasing(private val bounces: Int = 4, decay: Double = 0.5) : Easing {
    private val heightDecay = 1 - decay
    private val timeDecay = sqrt(heightDecay)
    internal val totalTime = (0 until bounces).sumOf { timeDecay.pow(it) }

    fun totalDuration(firstBounceDuration: Long) = (firstBounceDuration * totalTime).toLong()

    override fun transform(fraction: Float): Float {
        if (fraction <= 0 || fraction >= 1) return 0f

        // determine which bounce this is (and the x offset)
        var inputOffset = 0.0
        var bounce = 0
        while (bounce < bounces) {
            val bounceDuration = timeDecay.pow(bounce) / totalTime
            if (fraction <= inputOffset + bounceDuration) {
                // current bounce, center the quadratic for this bounce and quit looping
                inputOffset += bounceDuration / 2
                break
            }
            inputOffset += bounceDuration
            bounce++
        }

        // base quadratic "-4x^2" shifted & scaled to fill each bounce's segment
        val x = fraction - inputOffset
        val q = -4 * x * x
        val output = q * totalTime * totalTime
        return (output + heightDecay.pow(bounce)).toFloat()
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractBounceEasingTest"`
Expected: PASS

- [ ] **Step 5: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer
git commit -m "Port BounceInterpolator as TractBounceEasing"
```

---

### Task 7: `TractPageLayout` — static measure & placement

**Files:**
- Create: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/TractPageLayout.kt`
- Test: `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/tract/TractPageLayoutTest.kt`

**Interfaces:**
- Consumes: `TractPageState` (Task 2), alignment lines (Task 5).
- Produces (used by Tasks 8–9):

```kotlin
@Composable
internal fun TractPageLayout(
    pageState: TractPageState,
    hero: @Composable () -> Unit,
    callToAction: @Composable () -> Unit,
    card: @Composable (TractPage.Card) -> Unit,
    modifier: Modifier = Modifier,
    callToActionTip: (@Composable () -> Unit)? = null,
    onCardSwiped: () -> Unit = {},
)
```

In this task positions SNAP to targets (no animation yet); `onCardSwiped` is unused until Task 8.

- [ ] **Step 1: Write the failing tests**

Tests compose a fixed-size layout (400×800dp) with simple fixed-size slot contents plus real `RenderTractCard`s, and assert relative geometry via `getBoundsInRoot()`:

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class TractPageLayoutTest : BaseRendererTest() {
    private val page = TractPage(
        cards = { page ->
            (0..2).map { TractPage.Card(page, it, label = { p -> Text(p, text = "Card $it") }) }
        },
    )

    private val layoutHeight = 800.dp

    @Composable
    private fun TestLayout(pageState: TractPageState) = TractPageLayout(
        pageState = pageState,
        hero = { Box(Modifier.fillMaxSize().testTag("hero")) },
        callToAction = { Box(Modifier.height(56.dp).testTag("cta")) },
        card = { card -> RenderTractCard(card, state, {}, {}, {}) },
        modifier = Modifier.size(400.dp, layoutHeight),
    )

    @Test
    fun `hero state - hero visible, cards stacked at bottom`() = runComposeUiTest {
        val pageState = TractPageState(page)
        setContent { ProvideTestCompositionLocals { TestLayout(pageState) } }

        val heroBounds = onNodeWithTag("hero").getBoundsInRoot()
        assertTrue(heroBounds.top == 0.dp, "hero should be at the top")

        // all card tops should be near the bottom, first card's label above the others
        val cards = onAllNodesWithTag(TestTagTractCard).fetchSemanticsNodes()
        val cardTops = (0 until cards.size).map {
            onAllNodesWithTag(TestTagTractCard)[it].getBoundsInRoot().top
        }
        assertTrue(cardTops.all { it > layoutHeight / 2 }, "stacked cards should sit in the bottom half")
        assertTrue(cardTops[0] < cardTops[1] && cardTops[1] < cardTops[2], "labels should stack in order")
    }

    @Test
    fun `active card state - active card open, next card peeking, hero offscreen`() = runComposeUiTest {
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[0]) }
        setContent { ProvideTestCompositionLocals { TestLayout(pageState) } }

        val heroBounds = onNodeWithTag("hero").getBoundsInRoot()
        assertTrue(heroBounds.bottom <= 0.dp, "hero should be above the viewport")

        val activeTop = onAllNodesWithTag(TestTagTractCard)[0].getBoundsInRoot().top
        assertTrue(activeTop == 0.dp, "active card should be at the top")

        val peekTop = onAllNodesWithTag(TestTagTractCard)[1].getBoundsInRoot().top
        assertTrue(peekTop > layoutHeight - 100.dp, "next card should peek at the bottom edge")

        val thirdTop = onAllNodesWithTag(TestTagTractCard)[2].getBoundsInRoot().top
        assertTrue(thirdTop >= layoutHeight, "later cards should be below the viewport")
    }

    @Test
    fun `call to action - at bottom and visible only when no later card exists`() = runComposeUiTest {
        val pageState = TractPageState(page).apply { navigateToCard(page.cards[2]) }
        setContent { ProvideTestCompositionLocals { TestLayout(pageState) } }

        val ctaBounds = onNodeWithTag("cta").getBoundsInRoot()
        assertTrue(ctaBounds.bottom == layoutHeight, "CTA should sit at the bottom edge")
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractPageLayoutTest"`
Expected: compile FAIL.

- [ ] **Step 3: Write the implementation**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

internal sealed interface TractPageLayoutSlot {
    data object Hero : TractPageLayoutSlot
    data object CallToAction : TractPageLayoutSlot
    data object CallToActionTip : TractPageLayoutSlot
    data class Card(val id: String) : TractPageLayoutSlot
}

@Composable
internal fun TractPageLayout(
    pageState: TractPageState,
    hero: @Composable () -> Unit,
    callToAction: @Composable () -> Unit,
    card: @Composable (TractPage.Card) -> Unit,
    modifier: Modifier = Modifier,
    callToActionTip: (@Composable () -> Unit)? = null,
    onCardSwiped: () -> Unit = {},
) {
    val visibleCards = pageState.visibleCards

    Layout(
        content = {
            Box(Modifier.layoutId(TractPageLayoutSlot.Hero)) { hero() }
            visibleCards.forEach { c ->
                key(c.id) { Box(Modifier.layoutId(TractPageLayoutSlot.Card(c.id))) { card(c) } }
            }
            Box(Modifier.layoutId(TractPageLayoutSlot.CallToAction)) { callToAction() }
            callToActionTip?.let { Box(Modifier.layoutId(TractPageLayoutSlot.CallToActionTip)) { it() } }
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val loose = constraints.copy(minWidth = 0, minHeight = 0)

        // 1. measure the call to action first — its height reduces card/hero space
        val cta = measurables.firstOrNull { it.layoutId == TractPageLayoutSlot.CallToAction }?.measure(loose)
        val ctaTip = measurables.firstOrNull { it.layoutId == TractPageLayoutSlot.CallToActionTip }?.measure(loose)
        val ctaHeight = cta?.height ?: 0

        // 2. measure cards backwards, accumulating the stacked label heights
        val cardMeasurables = visibleCards.map { c ->
            measurables.first { it.layoutId == TractPageLayoutSlot.Card(c.id) }
        }
        val cardPlaceables = arrayOfNulls<Placeable>(cardMeasurables.size)
        val peekLine = IntArray(cardMeasurables.size)
        val stackLine = IntArray(cardMeasurables.size)
        val siblingStackOffset = IntArray(cardMeasurables.size)
        var nextCardPeek = 0
        var cardStackHeight = 0
        var firstCardPadding = 0
        for (i in cardMeasurables.indices.reversed()) {
            val heightUsed = maxOf(nextCardPeek, ctaHeight)
            val placeable = cardMeasurables[i]
                .measure(loose.copy(maxHeight = (height - heightUsed).coerceAtLeast(0)))
            val padding = placeable[TractCardPaddingLine].orZero()
            peekLine[i] = placeable[TractCardPeekLine].orZero()
            stackLine[i] = placeable[TractCardStackLine].orZero()
            siblingStackOffset[i] = cardStackHeight
            cardStackHeight += stackLine[i] - padding
            nextCardPeek = peekLine[i]
            firstCardPadding = padding
            cardPlaceables[i] = placeable
        }

        // 3. measure the hero with the space used by the card stack (or the CTA when there are no cards)
        val heroHeightUsed = if (cardMeasurables.isNotEmpty()) cardStackHeight + firstCardPadding else ctaHeight
        val heroPlaceable = measurables.firstOrNull { it.layoutId == TractPageLayoutSlot.Hero }
            ?.measure(loose.copy(maxHeight = (height - heroHeightUsed).coerceAtLeast(0)))

        // 4. target positions for the current active card (port of PageContentLayout.getChildTargetY)
        val activePosition = pageState.activeCardPosition
        val ctaVisible = activePosition + 1 >= visibleCards.size

        layout(width, height) {
            heroPlaceable?.placeRelative(0, if (activePosition < 0) 0 else -height)
            cardPlaceables.forEachIndexed { i, placeable ->
                placeable ?: return@forEachIndexed
                val y = when {
                    activePosition < 0 -> height - stackLine[i] - siblingStackOffset[i]
                    i < activePosition -> -height
                    i == activePosition -> 0
                    i == activePosition + 1 -> height - peekLine[i]
                    else -> height
                }
                placeable.placeRelative(0, y)
            }
            cta?.placeRelativeWithLayer(0, height - ctaHeight) { alpha = if (ctaVisible) 1f else 0f }
            ctaTip?.placeRelativeWithLayer(
                CallToActionTipStartMargin.roundToPx(),
                height - ctaHeight - ctaTip.height,
            ) { alpha = if (ctaVisible) 1f else 0f }
        }
    }
}

private val CallToActionTipStartMargin = 24.dp

private fun Int.orZero() = takeIf { it != AlignmentLine.Unspecified } ?: 0
```

(Add missing imports: `androidx.compose.foundation.layout.Box`, `androidx.compose.ui.unit.dp`. `placeable[line]` returns `AlignmentLine.Unspecified` when the child didn't publish the line — `orZero()` guards that.)

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractPageLayoutTest"`
Expected: PASS. Iterate on placement math until the geometry assertions hold.

- [ ] **Step 5: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer
git commit -m "Add TractPageLayout stacked card layout"
```

---

### Task 8: `TractPageLayout` — animation, gestures, bounce hint

**Files:**
- Modify: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/TractPageLayout.kt`
- Test: extend `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/tract/TractPageLayoutTest.kt`

**Interfaces:**
- Produces: same `TractPageLayout` signature; `onCardSwiped` now fires on successful fling navigation. Positions/CTA alpha animate between states; bounce hint runs while `pageState.isBounceFirstCard`.

**Design:** placement reads per-slot `Animatable` Y offsets from a remembered coordinator; the measure pass publishes targets into a `mutableStateOf` targets snapshot; a `LaunchedEffect` animates or snaps toward new targets.

- [ ] **Step 1: Write the failing tests**

Add to `TractPageLayoutTest`:

```kotlin
@Test
fun `fling up opens the first card`() = runComposeUiTest {
    val pageState = TractPageState(page)
    var swiped = 0
    setContent {
        ProvideTestCompositionLocals {
            TractPageLayout(
                pageState = pageState,
                hero = { Box(Modifier.fillMaxSize().testTag("hero")) },
                callToAction = { Box(Modifier.height(56.dp).testTag("cta")) },
                card = { card -> RenderTractCard(card, state, {}, {}, {}) },
                onCardSwiped = { swiped++ },
                modifier = Modifier.size(400.dp, layoutHeight).testTag("layout"),
            )
        }
    }

    onNodeWithTag("layout").performTouchInput { swipeUp() }
    waitForIdle()
    assertEquals(page.cards[0], pageState.activeCard)
    assertEquals(1, swiped)
}

@Test
fun `fling down closes the active card`() = runComposeUiTest {
    val pageState = TractPageState(page).apply { navigateToCard(page.cards[0]) }
    setContent {
        ProvideTestCompositionLocals {
            TractPageLayout(
                pageState = pageState,
                hero = { Box(Modifier.fillMaxSize().testTag("hero")) },
                callToAction = { Box(Modifier.height(56.dp).testTag("cta")) },
                card = { card -> RenderTractCard(card, state, {}, {}, {}) },
                modifier = Modifier.size(400.dp, layoutHeight).testTag("layout"),
            )
        }
    }

    onNodeWithTag("layout").performTouchInput { swipeDown() }
    waitForIdle()
    assertNull(pageState.activeCard)
}

@Test
fun `card positions animate between states`() = runComposeUiTest {
    mainClock.autoAdvance = false
    val pageState = TractPageState(page)
    setContent {
        ProvideTestCompositionLocals {
            TractPageLayout(
                pageState = pageState,
                hero = { Box(Modifier.fillMaxSize().testTag("hero")) },
                callToAction = { Box(Modifier.height(56.dp).testTag("cta")) },
                card = { card -> RenderTractCard(card, state, {}, {}, {}) },
                modifier = Modifier.size(400.dp, layoutHeight).testTag("layout"),
            )
        }
    }
    mainClock.advanceTimeBy(1_000)

    val stackedTop = onAllNodesWithTag(TestTagTractCard)[0].getBoundsInRoot().top
    pageState.navigateToCard(page.cards[0])
    mainClock.advanceTimeBy(100)
    val midTop = onAllNodesWithTag(TestTagTractCard)[0].getBoundsInRoot().top
    assertTrue(midTop < stackedTop && midTop > 0.dp, "card should be mid-animation, was $midTop")

    mainClock.advanceTimeBy(2_000)
    assertEquals(0.dp, onAllNodesWithTag(TestTagTractCard)[0].getBoundsInRoot().top)
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractPageLayoutTest"`
Expected: FAIL (no gesture handling; positions snap so the mid-animation assert fails).

- [ ] **Step 3: Implement animation coordinator, gestures & bounce**

Additions to `TractPageLayout.kt` (full new pieces shown; wire into the existing composable):

```kotlin
@Stable
private class TractPageLayoutAnimations {
    val offsets = mutableMapOf<TractPageLayoutSlot, Animatable<Float, AnimationVector1D>>()
    val ctaAlpha = Animatable(1f)
    var targets by mutableStateOf<Targets?>(null)

    data class Targets(
        val activeCardId: String?,
        val offsets: Map<TractPageLayoutSlot, Float>,
        val ctaVisible: Boolean,
    )

    fun offset(slot: TractPageLayoutSlot, initial: Float) = offsets.getOrPut(slot) { Animatable(initial) }
}

private val CardChangeAnimationSpec = tween<Float>(durationMillis = 300, easing = LinearOutSlowInEasing)
private const val BOUNCE_INITIAL_DELAY_MS = 2_000L
private const val BOUNCE_DELAY_MS = 7_000L
private const val BOUNCE_FIRST_BOUNCE_MS = 400
private val BounceHeight = 40.dp
private val FlingVelocityThreshold = 1_000.dp // ≈ ViewConfiguration min fling velocity × Android's ×20 factor
```

In the composable, before `Layout`:

```kotlin
val animations = remember(pageState) { TractPageLayoutAnimations() }

// animate/snap to new targets published by the measure pass
LaunchedEffect(animations) {
    var lastActiveCard: String? = pageState.activeCard?.id
    var initial = true
    snapshotFlow { animations.targets }.filterNotNull().collect { targets ->
        val animate = !initial && targets.activeCardId != lastActiveCard
        lastActiveCard = targets.activeCardId
        initial = false
        coroutineScope {
            targets.offsets.forEach { (slot, y) ->
                launch {
                    val offset = animations.offset(slot, y)
                    if (animate) offset.animateTo(y, CardChangeAnimationSpec) else offset.snapTo(y)
                }
            }
            // CTA fade-out runs concurrently with offsets; fade-in is sequenced after them
            if (!targets.ctaVisible) {
                launch {
                    if (animate) animations.ctaAlpha.animateTo(0f, CardChangeAnimationSpec)
                    else animations.ctaAlpha.snapTo(0f)
                }
            } else if (!animate) {
                launch { animations.ctaAlpha.snapTo(1f) }
            }
        }
        if (targets.ctaVisible && animate) animations.ctaAlpha.animateTo(1f, CardChangeAnimationSpec)
    }
}

// bounce hint: first card bounces after 2s (then every 7s) while enabled, no card active, no animation running
val bounceHeightPx = with(LocalDensity.current) { BounceHeight.toPx() }
LaunchedEffect(animations, pageState) {
    snapshotFlow { pageState.isBounceFirstCard && pageState.activeCard == null && pageState.visibleCards.isNotEmpty() }
        .collectLatest { enabled ->
            if (!enabled) return@collectLatest
            val easing = TractBounceEasing()
            val duration = easing.totalDuration(BOUNCE_FIRST_BOUNCE_MS.toLong()).toInt()
            delay(BOUNCE_INITIAL_DELAY_MS)
            while (true) {
                val slot = TractPageLayoutSlot.Card(pageState.visibleCards.first().id)
                val offset = animations.offsets[slot]
                if (offset != null && !offset.isRunning) {
                    val base = offset.value
                    animate(0f, 1f, animationSpec = tween(duration, easing = LinearEasing)) { t, _ ->
                        launch { offset.snapTo(base - bounceHeightPx * easing.transform(t)) }
                    }
                    offset.snapTo(base)
                }
                delay(BOUNCE_DELAY_MS)
            }
        }
}

// gestures
val flingThresholdPx = with(LocalDensity.current) { FlingVelocityThreshold.toPx() }
fun handleFling(velocityY: Float): Boolean = when {
    velocityY >= flingThresholdPx && pageState.activeCardPosition >= 0 ->
        pageState.previousCard().also { if (it) onCardSwiped() }
    velocityY <= -flingThresholdPx && pageState.activeCardPosition < pageState.visibleCards.size - 1 ->
        pageState.nextCard().also { if (it) onCardSwiped() }
    else -> false
}
val nestedScrollConnection = remember(pageState) {
    object : NestedScrollConnection {
        override suspend fun onPreFling(available: Velocity) =
            if (handleFling(available.y)) available else Velocity.Zero
    }
}
```

The `Layout`'s modifier becomes:

```kotlin
modifier = modifier
    .nestedScroll(nestedScrollConnection)
    .pointerInput(pageState) {
        // raw flings on non-scrollable areas; ignore gestures starting in the bottom gutter
        val velocityTracker = VelocityTracker()
        var ignore = false
        detectVerticalDragGestures(
            onDragStart = { offset ->
                velocityTracker.resetTracking()
                val gutter = minOf(16.dp.toPx(), size.height / 10f)
                ignore = offset.y > size.height - gutter
            },
            onVerticalDrag = { change, _ -> velocityTracker.addPosition(change.uptimeMillis, change.position) },
            onDragEnd = { if (!ignore) handleFling(velocityTracker.calculateVelocity().y) },
        )
    },
```

The measure pass, instead of placing at computed `y` values directly, publishes targets and places at animated values:

```kotlin
// inside the measure lambda, replacing step 4's direct placement:
val targetOffsets = buildMap {
    heroPlaceable?.let { put(TractPageLayoutSlot.Hero, if (activePosition < 0) 0f else -height.toFloat()) }
    visibleCards.forEachIndexed { i, c ->
        val y = when {
            activePosition < 0 -> height - stackLine[i] - siblingStackOffset[i]
            i < activePosition -> -height
            i == activePosition -> 0
            i == activePosition + 1 -> height - peekLine[i]
            else -> height
        }
        put(TractPageLayoutSlot.Card(c.id), y.toFloat())
    }
}
val newTargets = TractPageLayoutAnimations.Targets(pageState.activeCard?.id, targetOffsets, ctaVisible)
if (animations.targets != newTargets) animations.targets = newTargets

layout(width, height) {
    heroPlaceable?.placeRelative(
        0,
        animations.offset(TractPageLayoutSlot.Hero, targetOffsets[TractPageLayoutSlot.Hero] ?: 0f)
            .value.roundToInt(),
    )
    cardPlaceables.forEachIndexed { i, placeable ->
        placeable ?: return@forEachIndexed
        val slot = TractPageLayoutSlot.Card(visibleCards[i].id)
        placeable.placeRelative(0, animations.offset(slot, targetOffsets.getValue(slot)).value.roundToInt())
    }
    cta?.placeRelativeWithLayer(0, height - ctaHeight) { alpha = animations.ctaAlpha.value }
    ctaTip?.placeRelativeWithLayer(
        CallToActionTipStartMargin.roundToPx(),
        height - ctaHeight - ctaTip.height,
    ) { alpha = animations.ctaAlpha.value }
}
```

Implementation notes:
- Writing `animations.targets` during measure is guarded by structural equality, so it cannot loop.
- If the bounce `animate {}` + `launch { snapTo }` combination proves awkward, an equivalent is a manual `withFrameNanos` loop calling `offset.snapTo(...)` — behavior over mechanism; the test is the contract.
- Fling sign convention: Compose pointer velocity is positive downward. `swipeUp()` produces negative `y` velocity → next card. The tests pin this; if they fail invert the comparisons, not the tests.
- Remove offsets for cards no longer in `visibleCards` from `animations.offsets` (`keys.retainAll`) to avoid leaking slots.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.TractPageLayoutTest"`
Expected: PASS (all Task 7 + Task 8 tests).

- [ ] **Step 5: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer
git commit -m "Add TractPageLayout animations and card gestures"
```

---

### Task 9: `RenderTractPage` assembly

**Files:**
- Create: `module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractPage.kt`
- Modify: `module/parser/src/commonMain/kotlin/org/cru/godtools/shared/tool/parser/model/tract/Modal.kt:79-84` (add `listeners` parameter to the `@RestrictTo(TESTS)` constructor)
- Test: `module/renderer/src/commonTest/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractPageTest.kt`

**Interfaces:**
- Consumes: everything from Tasks 1–8 plus `RenderTractHero`, `RenderBackground`, `ContentEventListener`, `ProvideLayoutDirectionFromLocale`, `TipUpArrow`, `triggerScreenView`.
- Produces (public API):

```kotlin
@Composable
fun RenderTractPage(
    page: TractPage,
    modifier: Modifier = Modifier,
    contentInsets: PaddingValues = PaddingValues(0.dp),
    state: State = remember { State() },
    pageState: TractPageState = rememberTractPageState(page),
    pageEvents: (TractPageEvent) -> Unit = {},
)

sealed interface TractPageEvent {
    data class ActiveCardChanged(val card: TractPage.Card?) : TractPageEvent
    data object GoToNextPage : TractPageEvent
    data object CardTapped : TractPageEvent
    data object CardSwiped : TractPageEvent
}
```

Plus `internal const val TestTagTractPage = "TractPage"` and `internal val TractPageId: SemanticsPropertyKey<String>`.

- [ ] **Step 1: Write the failing tests**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.test.runCurrent
import org.ccci.gto.support.androidx.test.junit.runners.AndroidJUnit4
import org.ccci.gto.support.androidx.test.junit.runners.RunOnAndroidWith
import org.cru.godtools.shared.renderer.BaseRendererTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.EventId
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.Modal
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

@RunOnAndroidWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTestApi::class)
class RenderTractPageTest : BaseRendererTest() {
    private val events = mutableListOf<TractPageEvent>()

    @Test
    fun `card label tap opens the card and emits events`() = runComposeUiTest {
        val page = TractPage(
            cards = { p -> listOf(TractPage.Card(p, 0, label = { c -> Text(c, text = "Card 1") })) },
        )
        val pageState = TractPageState(page)

        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(page, state = state, pageState = pageState, pageEvents = { events += it })
            }
        }

        onNodeWithText("Card 1").performClick()
        waitForIdle()
        assertEquals(page.cards[0], pageState.activeCard)
        assertEquals(
            listOf(TractPageEvent.CardTapped, TractPageEvent.ActiveCardChanged(page.cards[0])),
            events,
        )

        // tapping again closes it
        onNodeWithText("Card 1").performClick()
        waitForIdle()
        assertNull(pageState.activeCard)
    }

    @Test
    fun `hidden card revealed via navigateToCard is composed`() = runComposeUiTest {
        // Card's test constructor has no listeners parameter, so card-listener content events can't be
        // unit-tested directly (see "Deferred" section); this covers the reveal path the wiring calls.
        val page = TractPage(
            cards = { p -> listOf(TractPage.Card(p, 0), TractPage.Card(p, 1, isHidden = true)) },
        )
        val pageState = TractPageState(page)
        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(page, state = state, pageState = pageState, pageEvents = { events += it })
            }
        }

        pageState.navigateToCard(page.cards[1])
        waitForIdle()
        assertEquals(2, onAllNodesWithTag(TestTagTractCard).fetchSemanticsNodes().size)
    }

    @Test
    fun `content event - modal listener emits OpenModal state event`() = runComposeUiTest {
        val showModal = EventId(name = "show-modal")
        val page = TractPage(modals = { p -> listOf(Modal(p, listeners = setOf(showModal))) })

        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(page, state = state)
            }
        }

        state.events.filterIsInstance<State.Event.OpenModal>().test {
            state.triggerContentEvents(listOf(showModal))
            testScope.runCurrent()
            waitForIdle()
            assertEquals(State.Event.OpenModal(page.id, page.modals[0].id), awaitItem())
        }
    }

    @Test
    fun `call to action - arrow emits GoToNextPage`() = runComposeUiTest {
        val manifest = Manifest(pages = { listOf(TractPage(it), TractPage(it)) })
        val page = manifest.pages.first() as TractPage

        setContent {
            ProvideTestCompositionLocals {
                RenderTractPage(page, state = state, pageEvents = { events += it })
            }
        }

        onNodeWithContentDescription("Next Page").performClick()
        assertEquals(listOf<TractPageEvent>(TractPageEvent.GoToNextPage), events)
    }

    @Test
    fun `analytics - hero ScreenView fires while no card is active`() = runComposeUiTest {
        // build the page through a Manifest so page.position resolves to 0 (screen name "tool-0")
        val manifest = Manifest(code = "tool", pages = { listOf(TractPage(it)) })
        val page = manifest.pages.first() as TractPage

        state.events.filterIsInstance<State.Event.AnalyticsEvent.ScreenView>().test {
            setContent {
                ProvideTestCompositionLocals {
                    RenderTractPage(page, state = state)
                }
            }
            waitForIdle()
            assertEquals("tool-0", awaitItem().screenName)
        }
    }
}
```

`Modal`'s `@RestrictTo(TESTS)` constructor currently hardcodes `listeners = emptySet()`; extend it (matching the `dismissListeners` parameter style):

```kotlin
@RestrictTo(RestrictTo.Scope.TESTS)
constructor(
    page: TractPage = TractPage(),
    listeners: Set<EventId> = emptySet(),
    dismissListeners: Set<EventId> = emptySet(),
    title: ((Modal) -> Text?)? = null,
    content: ((Modal) -> List<Content>)? = null,
) : super(page) {
    this.page = page
    this.listeners = listeners
    this.dismissListeners = dismissListeners
    this.title = title?.invoke(this)
    this.content = content?.invoke(this).orEmpty()
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.RenderTractPageTest"`
Expected: compile FAIL.

- [ ] **Step 3: Write the implementation**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import kotlinx.coroutines.flow.drop
import org.cru.godtools.shared.renderer.RenderBackground
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.renderer.tips.TipUpArrow
import org.cru.godtools.shared.renderer.util.ContentEventListener
import org.cru.godtools.shared.renderer.util.ProvideLayoutDirectionFromLocale
import org.cru.godtools.shared.renderer.util.ProvideResumedLifecycleOwner
import org.cru.godtools.shared.renderer.util.triggerScreenView
import org.cru.godtools.shared.tool.analytics.ToolAnalyticsScreenNames
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

internal const val TestTagTractPage = "TractPage"
internal val TractPageId = SemanticsPropertyKey<String>(
    name = "TractPageId",
    mergePolicy = { parentValue, _ -> parentValue }
)

@Composable
fun RenderTractPage(
    page: TractPage,
    modifier: Modifier = Modifier,
    contentInsets: PaddingValues = PaddingValues(0.dp),
    state: State = remember { State() },
    pageState: TractPageState = rememberTractPageState(page),
    pageEvents: (TractPageEvent) -> Unit = {},
) = ProvideLayoutDirectionFromLocale(page.manifest.locale) {
    val currentPageEvents by rememberUpdatedState(pageEvents)

    // content event wiring
    ContentEventListener(state, page, pageState) { event ->
        page.cards.firstOrNull { event in it.listeners }?.let { pageState.navigateToCard(it) }
        if (pageState.activeCard?.dismissListeners?.contains(event) == true) pageState.dismissActiveCard()
        page.modals.firstOrNull { event in it.listeners }
            ?.let { state.triggerEvent(State.Event.OpenModal(page.id, it.id)) }
    }

    // surface active card changes to the host (live share, host analytics)
    LaunchedEffect(pageState) {
        snapshotFlow { pageState.activeCard }
            .drop(1)
            .collect { currentPageEvents(TractPageEvent.ActiveCardChanged(it)) }
    }

    Box(
        modifier
            .testTag(TestTagTractPage)
            .semantics { this[TractPageId] = page.id }
    ) {
        RenderBackground(page.background, Modifier.matchParentSize())

        val showTips by state.showTips.collectAsState()
        TractPageLayout(
            pageState = pageState,
            hero = {
                ProvideResumedLifecycleOwner(resumed = pageState.activeCard == null) {
                    LifecycleResumeEffect(page, state) {
                        state.triggerScreenView(page.manifest, ToolAnalyticsScreenNames.forTractPage(page))
                        onPauseOrDispose { }
                    }
                    RenderTractHero(
                        page,
                        state = state,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    )
                }
            },
            card = { card ->
                ProvideResumedLifecycleOwner(resumed = pageState.activeCard == card) {
                    RenderTractCard(
                        card,
                        state = state,
                        onToggleCard = {
                            currentPageEvents(TractPageEvent.CardTapped)
                            pageState.navigateToCard(card.takeUnless { it == pageState.activeCard })
                        },
                        onPreviousCard = { pageState.previousCard() },
                        onNextCard = { pageState.nextCard() },
                    )
                }
            },
            callToAction = {
                RenderTractCallToAction(page, onNextPage = { currentPageEvents(TractPageEvent.GoToNextPage) })
            },
            callToActionTip = page.callToAction.tip
                ?.takeIf { showTips }
                ?.let { tip -> { TipUpArrow(tip, state) } },
            onCardSwiped = { currentPageEvents(TractPageEvent.CardSwiped) },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentInsets)
        )
    }
}

sealed interface TractPageEvent {
    data class ActiveCardChanged(val card: TractPage.Card?) : TractPageEvent
    data object GoToNextPage : TractPageEvent
    data object CardTapped : TractPageEvent
    data object CardSwiped : TractPageEvent
}
```

Note: `State.triggerEvent` is `@RestrictTo(LIBRARY_GROUP)` — the renderer module is in the library group, matching existing usage in `RenderInlineTip`.

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.RenderTractPageTest"`
Expected: PASS

- [ ] **Step 5: Run the full renderer test suite**

Run: `./gradlew :module:renderer:testAndroidHostTest`
Expected: PASS (no regressions).

- [ ] **Step 6: Verify build & commit**

```bash
./gradlew assemble ktlintCheck
git add module/renderer module/parser
git commit -m "Add RenderTractPage composable"
```

---

### Task 10: Paparazzi snapshots & final verification

**Files:**
- Create: `module/renderer/src/androidHostTest/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractPagePaparazziTest.kt`

**Interfaces:**
- Consumes: `RenderTractPage`, `TractPageState`, `BasePaparazziTest` fixtures (`manifest`, `rtlManifest`, tips `ask`/`tip`, resources).

- [ ] **Step 1: Write the snapshot tests**

```kotlin
package org.cru.godtools.shared.renderer.tract

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import kotlin.test.Test
import org.cru.godtools.shared.renderer.BasePaparazziTest
import org.cru.godtools.shared.renderer.state.State
import org.cru.godtools.shared.tool.parser.model.Manifest
import org.cru.godtools.shared.tool.parser.model.Text
import org.cru.godtools.shared.tool.parser.model.tract.CallToAction
import org.cru.godtools.shared.tool.parser.model.tract.Header
import org.cru.godtools.shared.tool.parser.model.tract.Hero
import org.cru.godtools.shared.tool.parser.model.tract.TractPage

class RenderTractPagePaparazziTest : BasePaparazziTest() {
    private fun testPage(container: Manifest = manifest) = TractPage(
        container = container,
        header = { Header(it, number = { h -> Text(h, text = "1") }, title = { h -> Text(h, text = "Header Title") }) },
        hero = { Hero(it, heading = { h -> Text(h, text = "Hero Heading") }) },
        cards = { page ->
            (0..2).map { i -> TractPage.Card(page, i, label = { p -> Text(p, text = "Card ${i + 1}") }) }
        },
        callToAction = { CallToAction(it, label = { cta -> Text(cta, text = "Keep going!") }) },
    )

    @Test
    fun `RenderTractPage() - Hero with stacked cards`() = contentSnapshot {
        RenderTractPage(testPage(), modifier = Modifier.fillMaxSize())
    }

    @Test
    fun `RenderTractPage() - Active first card with peek`() = contentSnapshot {
        val page = testPage()
        RenderTractPage(
            page,
            pageState = TractPageState(page).apply { navigateToCard(page.cards[0]) },
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Test
    fun `RenderTractPage() - Active last card shows call to action`() = contentSnapshot {
        val page = testPage()
        RenderTractPage(
            page,
            pageState = TractPageState(page).apply { navigateToCard(page.cards[2]) },
            modifier = Modifier.fillMaxSize(),
        )
    }

    @Test
    fun `RenderTractPage() - No cards shows call to action under hero`() = contentSnapshot {
        val page = TractPage(
            container = manifest,
            hero = { Hero(it, heading = { h -> Text(h, text = "Hero Heading") }) },
            callToAction = { CallToAction(it, label = { cta -> Text(cta, text = "Keep going!") }) },
        )
        RenderTractPage(page, modifier = Modifier.fillMaxSize())
    }

    @Test
    fun `RenderTractPage() - RTL`() = contentSnapshot {
        RenderTractPage(testPage(rtlManifest), modifier = Modifier.fillMaxSize())
    }

    @Test
    fun `RenderTractPage() - Tips enabled`() = contentSnapshot {
        val page = TractPage(
            container = manifest,
            hero = { Hero(it, heading = { h -> Text(h, text = "Hero Heading") }) },
            cards = { p -> listOf(TractPage.Card(p, 0, label = { c -> Text(c, text = "Card 1") })) },
            callToAction = { CallToAction(it, label = { cta -> Text(cta, text = "Keep going!") }, tip = "tip") },
        )
        RenderTractPage(page, state = State().apply { showTips.value = true }, modifier = Modifier.fillMaxSize())
    }
}
```

(Check `Header`/`Hero` test-constructor parameter names in `module/parser/.../tract/` and adjust. Card tips require content tips — if wiring a card tip fixture is awkward via test constructors, the tips-enabled snapshot covering the CTA tip arrow is sufficient; the card indicator is covered by `RenderTractCardTest`.)

- [ ] **Step 2: Verify the tests compile and fail only on missing goldens**

Run: `./gradlew :module:renderer:testAndroidHostTest --tests "org.cru.godtools.shared.renderer.tract.RenderTractPagePaparazziTest" verifyPaparazzi 2>&1 | tail -30`
Expected: FAIL with missing/mismatched snapshot images (NOT compile errors or crashes).

- [ ] **Step 3: Full verification**

```bash
./gradlew assemble
./gradlew :build-logic:ktlintCheck ktlintCheck
./gradlew :module:renderer:testAndroidHostTest koverXmlReportAndroid
./gradlew iosSimulatorArm64Test jsTest
```
Expected: all PASS except the new Paparazzi tests (missing goldens).

- [ ] **Step 4: Commit**

```bash
git add module/renderer
git commit -m "Add RenderTractPage Paparazzi snapshot tests"
```

- [ ] **Step 5: Record snapshots via CI**

Invoke the `record-screenshots` skill to trigger the "Record Snapshots" workflow on this branch and fold the recorded images into the snapshot-test commit.

- [ ] **Step 6: Verify recorded snapshots**

Run: `./gradlew verifyPaparazzi`
Expected: PASS

---

## Deferred / explicitly out of scope

- `animatedContentSnapshot` fling GIF test: attempt after Task 10 Step 6 if time allows, mirroring `RenderTipPaparazziTest`'s touch-robot pattern; it is a nice-to-have on top of the deterministic snapshots.
- Card `listeners` test-constructor support in the parser (needed to fully unit-test card-listener content events) — the wiring shares its code path with modal listeners, which are tested via the extended `Modal` test constructor; extend `Card`'s test constructor the same way only if review demands it.
- iOS `ComposeUIViewController` entry point for tract pages (pattern: `iosMain/.../content/ContentStackView.kt`) — separate feature.
