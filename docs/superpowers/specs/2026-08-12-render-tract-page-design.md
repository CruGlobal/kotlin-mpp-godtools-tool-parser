# RenderTractPage Design

**Date:** 2026-08-12
**Status:** Approved

## Goal

Add a `RenderTractPage` composable to `module/renderer` that renders a single Tract page with full
interactive parity with the Android implementation
([godtools-android `ui/tract-renderer`](https://github.com/CruGlobal/godtools-android/tree/develop/ui/tract-renderer)),
so godtools-android can eventually replace its view-based `PageController`/`PageContentLayout` with this
shared composable.

A Tract page is a layered stack: full-bleed background → hero (scrollable header + content) → cards that
slide up from the bottom (stacked/peeking, one "active" at a time) → a call-to-action row pinned at the
bottom. The Android app already delegates hero/card/modal *content* rendering to this repo
(`RenderTractHero`, `RenderTractCardContent`, `RenderTractModal`); this design adds the page-level
composition: card chrome, the stacked-card layout engine with gestures and animations, the call-to-action,
and event/analytics wiring.

## Scope decisions

- **Full interactive parity**: stacked cards with label bars, tap + fling navigation, animated card
  transitions, hidden-card event handling, call-to-action fade, and the periodic first-card bounce hint.
- **Modals**: the host handles them. `RenderTractPage` emits a `State.Event.OpenModal(pageId, modalId)`
  through the existing `State.events` flow (the same channel as `OpenTip`); the host resolves the modal
  via `(manifest.findPage(pageId) as? TractPage)?.findModal(modalId)` and renders `RenderTractModal`
  wherever it wants (e.g. full-screen above its toolbar), matching current Android architecture. Ids are
  carried instead of the `Modal` object to stay consistent with `OpenTip`, keep payloads
  platform-friendly, and avoid retaining parser models in the buffered event flow. This adds the new
  `Event.OpenModal` subclass (+ tests) to `module/renderer-state`.
- **Tips**: full tips UI — card label-row tip indicators and the call-to-action tip arrow, gated on tips
  being enabled (`State.showTips`). `RenderTractHero` already handles the header tip.
- **Active-card state**: hoisted into a `@Stable` saveable `TractPageState` (mirroring
  `LessonPagerState`) so hosts can observe the active card and navigate imperatively (live share).
- **Layout engine**: a custom `Layout` with a `MeasurePolicy` porting `PageContentLayout` semantics,
  using Compose alignment lines for card anchor measurement (chosen over `Box` + `onSizeChanged` offsets,
  which is multi-frame and snapshot-hostile, and `AnchoredDraggable`, which adds drag-tracking beyond
  Android parity while complicating multi-card coordination).

## Public API

`module/renderer/src/commonMain/kotlin/org/cru/godtools/shared/renderer/tract/RenderTractPage.kt`,
following `RenderLessonPage` conventions:

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
```

- Wraps content in `ProvideLayoutDirectionFromLocale(page.manifest.locale)`.
- Renders `RenderBackground(page.background)` full-bleed behind everything; the background ignores
  `contentInsets` while page content respects them (on Android the page layout sits below the action bar
  but the background bleeds under it).

### TractPageEvent

Sealed interface in the same file (mirroring `LessonPageEvent`):

| Event | Purpose |
|---|---|
| `ActiveCardChanged(card: TractPage.Card?)` | Live-share publishing and host screen tracking |
| `GoToNextPage` | Call-to-action arrow tapped |
| `CardTapped` | User toggled a card by tap — host records `FEATURE_TRACT_CARD_CLICKED` |
| `CardSwiped` | User navigated cards by fling — host records `FEATURE_TRACT_CARD_SWIPED` |

Tip taps and modal requests do not go through `pageEvents` — they use the existing `State` event flow
(`OpenTip`, and the new `OpenModal`), keeping `pageEvents` limited to genuinely page-scoped concerns.

### TractPageState

`tract/TractPageState.kt`, `@Stable`, with `rememberTractPageState(page)` and a `Saver` persisting the
active card id and enabled hidden-card ids:

- `activeCard: TractPage.Card?` — read-only externally.
- `visibleCards: List<TractPage.Card>` — `page.cards` filtered to non-hidden or event-enabled cards.
- `navigateToCard(card: TractPage.Card?)` — enables hidden cards as needed; the live-share subscriber
  entry point.
- `nextCard()` / `previousCard()` / `dismissActiveCard()` — navigation used by card chrome and events.
- `isBounceFirstCard: Boolean` — host-settable (Android sets it from `isVisible && !cardsDiscovered`);
  defaults to `false`.
- Deactivating an event-enabled hidden card re-hides it (Android's `hideHiddenCardsThatArentActive`).
- `updatePage(page)` — supports the host language switcher: when a different `TractPage` instance for the
  same logical page (a parallel translation) is provided, the state keeps the active card and enabled
  hidden cards. The active card is tracked as an id (`activeCardId`) — the single source of truth — with
  `activeCard` derived from it against the current page's cards (card ids are
  `"${page.id}-card-$position"`, which line up across translations), so `updatePage` swaps the page and
  the derivation re-resolves automatically. `rememberTractPageState` therefore does not key
  `rememberSaveable` on the page — it applies `updatePage` on recomposition, mirroring
  `LessonPagerState.updateManifest`. When the new page has no card matching the active card id,
  `updatePage` resets to the hero permanently via `navigateToCard(null)` — switching back later does not
  restore the card, and a revealed hidden card re-hides (routing through `navigateToCard` keeps the
  enabled-hidden-cards invariant intact).

## Layout engine: TractPageLayout

Internal custom `Layout` (`tract/TractPageLayout.kt`) with four child roles tagged via parent data:
**hero**, **cards** (one per visible card, in order), **call-to-action**, and **call-to-action tip
arrow**.

### Anchor measurement via alignment lines

Android's `PageContentLayout` needed a post-layout hack (`onGlobalLayout` + `requestLayout`) to learn
where each card's label bar sits. Compose has a first-class mechanism: `RenderTractCard` publishes three
`HorizontalAlignmentLine`s from its internal layout —

- `TractCardPadding` — top of the card surface (below the transparent margin)
- `TractCardPeek` — top of the label text (matching Android's `layout_card_peek_toTopOf="@id/label"`)
- `TractCardStack` — top of the label divider (matching Android's `layout_card_stack_toTopOf`; in the
  stacked state the next card's surface covers the divider entirely, so stacked cards show only their
  margin + label row with no divider lines visible)

The parent measure policy reads them off each card's `Placeable`. Deterministic, single measure pass, no
feedback loops, snapshot-testable on the first frame.

### Measure pass (port of `PageContentLayout.onMeasure`)

1. Measure the call-to-action → `ctaHeight`.
2. Measure cards **backwards**: each card gets max height reduced by
   `max(next card's peek height, ctaHeight)`; accumulate stack height from each card's stack/padding
   anchors so stacked label bars pile up above the bottom edge.
3. Measure the hero with height reduced by the card stack height (or `ctaHeight` when the page has no
   cards).

### Placement + animation

The measure policy computes each child's *target* Y for the current `activeCard`, porting
`getChildTargetY`:

- hero on-screen when no active card, else pushed above the viewport
- cards before the active card: above the viewport
- active card: at its layout position
- card after the active card: peeking its label above the bottom edge
- remaining cards: fully below the viewport
- no active card: all cards stacked at the bottom

Children whose animated position leaves them fully outside the viewport are not placed at all — they stay
composed and measured (state, lifecycle, and alignment lines unaffected) but are skipped for drawing,
hit-testing, and semantics until their animated position intersects the viewport again. (This is a
deliberate improvement over Android, which laid out all children and relied on translation + clipping.)

Each card and the hero own an `Animatable` Y offset, remembered per card id in a small coordinator class.
A `LaunchedEffect` animates children to new targets when `activeCard` changes, then fades the
call-to-action in (Android sequences offset → CTA fade-in; CTA/tip fade-*out* runs concurrently with
offsets). The placement lambda reads the animatable values so animation frames only re-run placement, not
measure. The CTA and tip arrow are visible only when `activeCardPosition + 1 >= totalCards` (i.e. no card
after the active one), matching `getChildTargetAlpha`.

### Gestures

- **Tap**: handled in card chrome — the label-row region toggles the card (emits `CardTapped`).
- **Fling from scrollable content** (hero or open card): a page-level `NestedScrollConnection` maps
  unconsumed vertical fling velocity to card navigation — fling up opens the next card (including opening
  the first card from the hero), fling down closes toward the previous card/hero. Threshold ports
  Android's `minimumFlingVelocity × 20`.
- **Raw flings on non-scrollable areas**: a `pointerInput` fling detector on the layout, ignoring
  gestures starting in the bottom gutter (`min(16.dp, height / 10)`, per Android).
- Successful fling navigation emits `CardSwiped`.

### Bounce hint

While `isBounceFirstCard` is true, no card is active, and no other animation is running: after an initial
2s delay (then every 7s), the first card's Y animatable plays a 40dp bounce — a port of Android's
`BounceInterpolator` (4 bounces, 0.5 decay, 400ms first bounce) as a custom `Easing`. Runs in the
coordinator's coroutine scope; cancelled on state change (a card-change animation supersedes it), with the
card snapped back to its base position on cancellation so it is never stranded mid-bounce.

### Child lifecycles

A small internal helper (generalizing the approach of `ProvideCurrentPageLifecycleOwner`) caps each
child's `LifecycleOwner` at `STARTED` unless it's "current": the hero is RESUMED only when no card is
active; each card is RESUMED only while it's the active card. Existing `LifecycleResumeEffect`-based
behavior in content (analytics, videos, animations) then works unchanged.

## Card chrome: RenderTractCard

Internal Compose port of `tract_content_card.xml`: a transparent 16dp margin frame (Android's
`card_margin_horiz/top/bottom`) around a Material3 `ElevatedCard` (default M3 shape,
`ToolTheme.cardElevation()`, consolidating with the renderer's other cards rather than Android's literal
10dp/6dp values) colored `card.backgroundColor` via `elevatedCardColors`, containing:

- **Label row**: `card.label` text (primary-colored, per model styling, 18sp), with a 24dp tip indicator icon
  at the end when tips are enabled and the card has tips (or is the last visible card and the page CTA
  has a tip). The indicator is purely visual — an icon for the tip type only, no completion-state lookup
  and no click handling of its own.
- **Divider**: 1dp, `card.textColor`, below the label row, inset 16dp horizontally (matching Android's
  `tract_content_margin_horizontal` margins — not full width).
- **Toggle tap target**: the entire region above the divider — label, indicator, and surrounding space —
  is one clickable area that toggles the card open/closed (emitting `CardTapped`), matching Android's
  full-width overlay `View`.
- **Content**: existing `RenderTractCardContent` (already has background, scroll + fading edge).
- **Bottom nav row**: "previous" / "1/6" / "next" using the manifest locale strings, with Material3
  `TextButton`s for previous/next (height-compensated so the row keeps its compact Android-equivalent
  height) and a plain centered position text; previous hidden on the first visible card, next hidden on
  the last. On hidden cards (`card.isHidden`,
  which stays true even while the card is revealed by a content event) all three elements — previous,
  the position text, and next — are suppressed, since `visiblePosition` is undefined for hidden cards.
  Matching Android's `invisibleIf` rules, hiding is invisible-style (space reserved, clicks disabled),
  not removal, keeping the card's content height and chrome measurements uniform; fling navigation off
  a revealed hidden card still works.
- The card surface publishes the three alignment lines (padding / peek / stack) consumed by
  `TractPageLayout`.

## Call-to-action: RenderTractCallToAction

Internal port of `tract_page_call_to_action.xml`: label text (32dp start margin) beside a 40dp arrow icon
(8dp end margin, 8dp internal padding) tinted `callToAction.controlColor`; arrow hidden on the last page;
tap → `GoToNextPage`. The arrow graphic is
ported from `ic_call_to_action.xml` as a Compose vector resource.

**CTA tip arrow** — a separate `TractPageLayout` child positioned above the CTA at the start edge (24dp
start margin), rendered with `TipDownArrow` (pointing down at the call to action; a vertical mirror of the
header's `TipUpArrow`), shown when `callToAction.tip != null` and tips are enabled, and only while the CTA
itself is visible (it shares the CTA's alpha/placement gating). This is the one tip surface on the page
that *is* interactive: it reflects done/not-done completion state from `LocalTipsRepository` and opens the
tip on tap (via the `State` `OpenTip` event).

## Event wiring

In `RenderTractPage` via `ContentEventListener(state)`:

- event id in a card's `listeners` → `pageState.navigateToCard(card)` (enables hidden cards)
- event id in the active card's `dismissListeners` → dismiss it
- event id in a modal's `listeners` → `state.triggerEvent(State.Event.OpenModal(page.id, modal.id))`

## Analytics

Self-contained (like `RenderLessonPage`), driven by the constrained child lifecycles:

- **Hero slot**: while no card is active, `LifecycleResumeEffect` → ScreenView
  `ToolAnalyticsScreenNames.forTractPage(page)` (in `RenderTractPage`'s hero slot); the hero
  `getAnalyticsEvents(VISIBLE)` triggering with delayed-event `Job` cancellation lives in the pre-existing
  `RenderTractHero` and activates through the slot's constrained lifecycle — delayed hero events are
  cancelled when a card opens or the page pauses before they
  fire. The hero slot is composed even when `page.hero` is null (it also renders the header), so the
  hero-state ScreenView always fires.
- **Card**: on becoming active/resumed → ScreenView `forTractPage(page, card)` +
  `card.getAnalyticsEvents(VISIBLE)`, with the same delayed-event cancellation on pause.

`RenderTractPage` itself fires no ScreenView of its own — the hero slot and cards cover every state.
(`TractPage` also has no page-level analytics events: `analyticsEvents = emptyList()`.)

## New strings

`module/renderer/src/commonMain/composeResources/values/strings_renderer.xml`, new Tract group using a
`tract_` feature prefix with `action` segments (consistent with the lesson/tip action naming):
`tract_card_action_previous`, `tract_card_action_next`, `tract_card_position` ("%1$d/%2$d"), and
`tract_accessibility_action_next_page` for the call-to-action arrow.

## Reused as-is

`RenderTractHero` (hero slot), `RenderTractCardContent`, `RenderBackground`, `ContentEventListener`,
`State.triggerScreenView`, `HasAnalyticsEvents.triggerAnalyticsEvents`, tips helpers (`TipIcon`,
`TipUpArrow`, `LocalTipsRepository`), `ProvideLayoutDirectionFromLocale`, `ToolTheme` tokens.

## Testing

- **`commonTest/tract/RenderTractPageTest`** (behavior, v2 `runComposeUiTest`): label tap toggles the
  card and emits `CardTapped`; hidden-card reveal; modal listeners emit `State.Event.OpenModal` on
  `state.events`; CTA tap emits `GoToNextPage`; fling emits `CardSwiped`; hero ScreenView; CTA tip arrow
  follows CTA visibility. (Card `listeners`/`dismissListeners` content-event branches are untestable
  until `Card`'s test constructor gains those params — documented fast-follow.)
- **`commonTest/tract/TractPageStateTest`**: state transitions, language-switch `updatePage` semantics +
  saver round-trip. **`commonTest/tract/TractPageLayoutTest`**: geometry, placement-skipping, gestures,
  animations, CTA visibility.
- **`androidHostTest/tract/RenderTractPagePaparazziTest`**: snapshots — hero with stacked cards, open
  card (first/last, showing next-card peek + nav row and CTA), no-cards page, RTL manifest, tips enabled
  (indicator + CTA tip arrow with an active card so they render), content insets over a background image,
  background with an active card; plus `animatedContentSnapshot` GIFs (open/next/dismiss card, bounce
  hint) mirroring `RenderTipPaparazziTest`.
- Project conventions: `./gradlew assemble` before committing (catches iOS/JS compile breaks), ktlint,
  snapshots recorded via the CI "Record Snapshots" workflow.
