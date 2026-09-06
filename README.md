# Stepathon — Onboarding UI Animation (Android)

Implementation of the onboarding UI animation from the supplied Figma prototype, built entirely with **Jetpack Compose**. The user swipes through three pages — a walking robot mascot, a trophy, and a reward box — with per-page gradients, mascot morph transitions, Lottie sparkles, a rotating starburst, and a shine-sweep CTA, ending in a "Let's Go" action.

| Page 1 — Step Up | Page 2 — Claim the Throne | Page 3 — Score Big!! |
| --- | --- | --- | 

---

## 1. The task

> Replicate the UI animation present in the prototype — a three-page onboarding flow for the Step-a-thon step-challenge app — using the supplied assets, and match the supplied reference screen recording.

## 2. Supplied assets used

| Asset | Used for |
| --- | --- |
| `ic_walking_robo.png`, `ic_trophy.png`, `ic_gift_box.png` | Page mascots (also the launcher icon) |
| `animation_star.json` (Lottie) | Sparkle twinkle overlay (renamed `animation_sparkle.json`) |
| `bg_rotating_star.webp` | Rotating starburst background on states 2–3 |
| Reference screen recording (`.mp4`) | Ground truth for the expected animation |


## 3. Implementation highlights

**One continuous pager value drives everything.** The pager state is a single `Animatable<Float>` (`pos`) where `0`, `1`, `2` are the pages and everything in between is a transition. Icon positions, sparkle translation/alpha, and the background gradient all derive from that one value, so the screen tracks the finger exactly and nothing falls out of sync.

**Custom `Layout` for the mascot morph (`IconStage`).** The transition is not a page-sized translation — the outgoing mascot scales down into a top-corner "preview" slot while the incoming one grows to center, with per-icon alpha and scale. `HorizontalPager` can't express this, so `IconStage` measures each mascot to the stage's square and places it along the center↔corner path, skipping placements that are fully invisible.

**State survives rotation.** `pos` and the derived `index` live in `OnboardingViewModel`. One Compose subtlety handled here: `Animatable.animateTo` needs a `MonotonicFrameClock`, which `viewModelScope` doesn't provide — so the ViewModel owns the state and drag *snaps* (`snapTo`), while settle animations (`animateTo`) run from the composition scope, which supplies the frame clock. Rotate the device mid-swipe and the position is preserved.

**Draw-phase animation reads.** Sparkle translation, starburst rotation, and the shine sweep read their animation values inside `graphicsLayer`/`drawBehind` — during the draw phase. Swiping animates the entire screen without recomposing the composition tree.

**Curved arched title (`CurvedText`).** Characters are measured individually, positioned along the arc by angle, and rotated tangentially on the native canvas, with a soft glow via `Paint.setShadowLayer` and a custom measure pass computing the arc's bounding box.

## 4. Assumptions made

- The supplied art (robot, trophy, gift box, sparkle Lottie, starburst) is the final art for all three pages.
- Three onboarding pages, matching the prototype flow, ending in a "Let's Go" action.
- The CTA is a navigation hook only — no next screen was in scope.
- No backend, persistence, or analytics was required.

## 5. Known limitations / what I'd do next

- **RTL**: swipe direction doesn't mirror for RTL locales (`draggable` deltas + arrow icon would need layout-direction awareness).
- **Memory**: the starburst asset is a 3135×3135 WebP decoded at intrinsic size (~37 MB bitmap). I'd downscale it to display size or load it with a size-constrained loader.
- **Tests**: none included. First coverage would be the swipe-settle logic (velocity + position → target page) and a screenshot test per page.
- **R8** is disabled in the release build type for simplicity; enable + tune keep rules before distribution.

*Built with Jetpack Compose. Mascot and decorative assets supplied with the task.*
