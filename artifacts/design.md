---
name: "Laterly — Nothing OS Design System"
version: "2.0.0"
updated: "2026-11-08"
---

# Laterly Design System — Nothing-Inspired

Laterly's visual language is derived from the Nothing OS design philosophy: monochrome, typographically driven, instrument-panel precision. Warm off-white in light mode. OLED black in dark mode. One red accent, used sparingly.

---

## Design Direction

- **Product:** A local-first read-later app for people who save links and articles to revisit with intention.
- **Character:** Quiet, exact, unhurried — like a well-organized notebook.
- **World:** Printed technical documentation. Manual on matte paper. Everything has a purpose.
- **Moments:** The item title on the detail screen (primary typography hero). The count number on the Collections screen (48sp Space Grotesk hero).
- **Signature:** Space Mono ALL CAPS metadata labels throughout — domain names, timestamps, counts. Instrument-panel tertiary text. One 4dp red dot under the active nav icon.

---

## Color Tokens

### Dark Mode (OLED)

| Token            | Hex       | Role                             |
| ---------------- | --------- | -------------------------------- |
| Background       | `#000000` | OLED black screen background     |
| Surface          | `#111111` | Cards, elevated surfaces         |
| Surface Raised   | `#1A1A1A` | Secondary elevation              |
| Border (subtle)  | `#222222` | Invisible hairlines              |
| Border (visible) | `#333333` | Intentional separators           |
| Text Disabled    | `#666666` | Timestamps, domain names, labels |
| Text Secondary   | `#999999` | Descriptions, metadata           |
| Text Primary     | `#E8E8E8` | Body text                        |
| Text Display     | `#FFFFFF` | Titles, headlines                |

### Light Mode (warm off-white)

| Token            | Hex       | Role                     |
| ---------------- | --------- | ------------------------ |
| Background       | `#F5F5F5` | Warm off-white page      |
| Surface          | `#FFFFFF` | Cards (subtle elevation) |
| Surface Raised   | `#F0F0F0` | Secondary elevation      |
| Border (subtle)  | `#E8E8E8` | Hairlines                |
| Border (visible) | `#CCCCCC` | Intentional separators   |
| Text Disabled    | `#999999` | Tertiary / labels        |
| Text Secondary   | `#666666` | Secondary body           |
| Text Primary     | `#1A1A1A` | Body text                |
| Text Display     | `#000000` | Headings                 |

### Accent & Status (identical in both modes)

| Token         | Hex                    | Role                                           |
| ------------- | ---------------------- | ---------------------------------------------- |
| Accent (red)  | `#D71921`              | Active nav dot, delete, save button, favorites |
| Accent Subtle | `rgba(215,25,33,0.15)` | Accent tint backgrounds                        |
| Success       | `#4A9E5C`              | Confirmed states                               |
| Warning       | `#D4A843`              | Caution states                                 |
| Interactive   | `#5B9BF6` / `#007AFF`  | Tappable text links (dark / light)             |

> **Red rule:** One accent per screen. The active nav dot, the favorite icon when active, the save button, and the delete action are all the same accent. Never use red decoratively.

---

## Typography

### Font Families

| Family        | Source       | License     | Role                     |
| ------------- | ------------ | ----------- | ------------------------ |
| Space Grotesk | Google Fonts | SIL OFL 1.1 | UI text, body, headings  |
| Space Mono    | Google Fonts | SIL OFL 1.1 | Labels, data, timestamps |

**Loading:** Android Downloadable Fonts via GMS provider (`@font/space_grotesk`, `@font/space_mono`). Fonts are declared in `res/font/` XML files. No bundled TTF assets needed — GMS handles caching.

### Type Scale

| Role            | Family        | Weight | Size | Letter Spacing | Use                            |
| --------------- | ------------- | ------ | ---- | -------------- | ------------------------------ |
| `headlineLarge` | Space Grotesk | Medium | 32sp | -0.5sp         | Hero moments, collection count |
| `headlineSmall` | Space Grotesk | Medium | 22sp | -0.25sp        | Screen titles, reader heading  |
| `titleLarge`    | Space Grotesk | Medium | 18sp | 0              | Card titles, detail title      |
| `titleMedium`   | Space Grotesk | Medium | 15sp | 0              | Feed item titles               |
| `titleSmall`    | Space Grotesk | Normal | 13sp | 0              | Secondary content titles       |
| `bodyLarge`     | Space Grotesk | Normal | 16sp | 0              | Reader view article text       |
| `bodyMedium`    | Space Grotesk | Normal | 14sp | 0              | Descriptions, settings rows    |
| `bodySmall`     | Space Mono    | Normal | 11sp | 0.5sp          | Secondary monospace text       |
| `labelMedium`   | Space Mono    | Normal | 11sp | 0.9sp          | ALL CAPS metadata labels       |
| `labelSmall`    | Space Mono    | Normal | 10sp | 1.0sp          | Smallest ALL CAPS labels       |

**Rule:** Space Mono labels are ALWAYS uppercase in code. Never lowercase Space Mono labels.

---

## Spacing

8px base grid. All padding, margin, gap values are multiples of 4px.

| Token | Value | Use                            |
| ----- | ----- | ------------------------------ |
| xs    | 4dp   | Icon-to-label tight gap        |
| sm    | 8dp   | Component internal             |
| md    | 16dp  | Standard padding               |
| lg    | 24dp  | Group separation               |
| xl    | 32dp  | Section breaks                 |
| 2xl   | 48dp  | Major section / hero breathing |

Screen horizontal padding: **20dp** everywhere.

---

## Corner Radii

| Context             | Radius | Rule                            |
| ------------------- | ------ | ------------------------------- |
| Cards               | 4dp    | Technical, slightly rounded     |
| Chips / filter tags | 4dp    | Consistent with cards           |
| Buttons             | 4dp    | Technical button shape          |
| Thumbnails          | 4dp    | Small image corners             |
| FAB                 | 4dp    | Square FAB, no pill             |
| Sheet / dialog      | 0dp    | Flat top edge for bottom sheets |

> Nothing design rule: no border-radius > 16dp on content containers. Buttons are never pill-shaped (9999dp) except in legacy M3 Expressive. Laterly 2.0 uses 4dp consistently.

---

## Borders & Dividers

- **Hairline dividers:** `1dp` lines using `outlineVariant` (or `NdBorder` token directly) between list rows.
- **No card elevation:** Cards use flat surface color, never `shadowElevation > 0`.
- **No decorative borders** around content groups — spacing alone separates sections.

---

## Iconography

- Material Icons (already in `material-icons-extended` dependency).
- Icon size: **20dp** in action areas, **18dp** in list rows, **22dp** in nav.
- Icon tint: always inherits text color role — `NdTextSecondary` for inactive, `onBackground` for active, `NdAccent` for destructive.
- No filled icons paired with outlined — use the same style throughout.
- No emoji as icons. The Collections screen uses a text initial letter instead.

---

## Components

### Bottom Navigation

- Flat bar, 1dp hairline top border.
- Icon-only tabs. No labels (reduces visual clutter, standard Nothing UX).
- Active indicator: 4×4dp red dot below the active icon. No filled pill.
- 3 tabs: Home, Collections, Search. Settings accessible from top-right icon.

### Filter Chips

- `RoundedCornerShape(4dp)`, flat surface.
- Space Mono ALL CAPS text, 10sp.
- Selected: `onBackground` fill + `background` text. Inverted monochrome, no color.
- Unselected: `surfaceContainer` fill + `NdTextSecondary` text.

### Feed Item Row

- Flat, no card background. 60×60dp thumbnail with 4dp corner.
- Hairline divider between rows.
- Three-layer hierarchy: domain+collection (Space Mono, 10sp, NdTextDisabled) → title (titleMedium, onBackground) → description (bodyMedium, NdTextSecondary).
- Tags: Space Mono inline text, no pill background. Max 3 visible.

### Cards (Collection, Detail)

- `RoundedCornerShape(4dp)`, `surfaceContainer` background.
- No elevation. No shadow.

### Buttons

- Primary action: `NdAccent` fill, `Color.White` text, `RoundedCornerShape(4dp)`.
- Secondary / text actions: `NdTextSecondary` color, no background.

### Bottom Sheet (Save flow)

- Flat top edge (`RoundedCornerShape(0dp)` on top).
- 2dp wide handle bar centered at top.
- Space Mono ALL CAPS sheet title.

### Tabs (Detail screen)

- Space Mono ALL CAPS tab label, 10sp.
- 2dp red underline on active tab.
- No filled tab backgrounds.

### Empty States

- No illustrations, no emoji, no sad faces.
- Space Mono `[EMPTY]` / `[NO RESULTS]` label + one-line bodyMedium explanation.
- Vertically centered.

### Loading

- Thin `CircularProgressIndicator` in accent red, 20dp size, 1dp stroke.
- Space Mono `[FETCHING...]` label below.

### Dialogs

- Material 3 `AlertDialog` with Nothing typography.
- Confirm button: `NdAccent` container.
- Dismiss button: `TextButton`.

---

## Motion

- No bounce, no spring, no parallax.
- `AnimationSpec`: `tween(200ms)`, `FastOutSlowInEasing`.
- Color and opacity transitions only. No layout animation.
- No `animate-pulse`, no looping animations on static elements.

---

## System Themes

The app follows the system dark/light preference via `isSystemInDarkTheme()` in `LaterlyTheme`. No manual override UI — settings screen notes "Follows system setting."

---

## Anti-patterns Removed in v2.0

| Was (M3 Expressive v1)       | Now (Nothing v2)                      |
| ---------------------------- | ------------------------------------- |
| Terracotta `#7A2B02` primary | Monochrome primary + `#D71921` accent |
| Amber `#F7931E` nav pill     | 4dp red dot indicator                 |
| Pill-shaped nav active tab   | Icon + dot, no pill                   |
| `shadowElevation = 8dp`      | Flat, `0dp` elevation                 |
| Emoji icons in collections   | Text initial letter                   |
| Gradient hero overlay        | Flat `Color.Black.copy(0.6f)` overlay |
| `FontFamily.SansSerif`       | Space Grotesk + Space Mono            |
| Light-only theme             | Dark + light, system-aware            |
| `CircleShape` pills (9999dp) | `RoundedCornerShape(4dp)` technical   |
| Colored badge chips          | Monochrome filter chips               |
| `Plus Jakarta Sans / Inter`  | Space Grotesk + Space Mono            |
