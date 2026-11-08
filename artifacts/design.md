---
name: "Albo: Save & Organize"
colors:
  surface: "#fcf8f6"
  surface-dim: "#ddd9d7"
  surface-bright: "#fcf8f6"
  surface-container-lowest: "#ffffff"
  surface-container-low: "#f7f3f1"
  surface-container: "#f4eeea"
  surface-container-high: "#ede6e1"
  surface-container-highest: "#e5e0db"
  on-surface: "#1c1b1a"
  on-surface-variant: "#6b5851"
  outline: "#897269"
  outline-variant: "#dcc1b6"
  primary: "#7a2b02" # Deep terracotta / rust brown (active chips, primary FABs)
  on-primary: "#ffffff"
  primary-container: "#9c4215"
  secondary: "#f7931e" # Bright amber / marigold (active bottom nav indicator)
  on-secondary: "#ffffff"
  secondary-container: "#fae3d2"
  badge-article: "#f39200" # Orange pill for Article / Collection
  badge-recipe: "#fae8dc" # Soft peach pill for Recipe
  badge-video: "#f7ded5" # Soft rose/peach pill for Video
  delete-tint: "#fcdcd9" # Soft red container for delete button
  delete-content: "#d32f2f"
  background: "#fcf8f6"
  on-background: "#1c1b1a"
typography:
  brand-wordmark:
    fontFamily: Plus Jakarta Sans / Outfit
    fontSize: 24px
    fontWeight: "700"
    letterSpacing: -0.02em
  screen-title:
    fontFamily: Plus Jakarta Sans / Inter
    fontSize: 20px
    fontWeight: "500"
  section-header:
    fontFamily: Plus Jakarta Sans / Inter
    fontSize: 18px
    fontWeight: "600"
  card-title:
    fontFamily: Plus Jakarta Sans / Inter
    fontSize: 15px
    fontWeight: "600"
    lineHeight: 20px
  card-subtitle:
    fontFamily: Plus Jakarta Sans / Inter
    fontSize: 13px
    fontWeight: "400"
    lineHeight: 18px
  badge:
    fontFamily: Plus Jakarta Sans / Inter
    fontSize: 11px
    fontWeight: "600"
  body-md:
    fontFamily: Plus Jakarta Sans / Inter
    fontSize: 14px
    fontWeight: "400"
    lineHeight: 20px
rounded:
  sm: 8px
  md: 14px
  card: 20px
  pill: 9999px
spacing:
  gutter: 12px
  margin: 16px
  card-padding: 14px
---

## Visual Source of Truth (Screenshots vs Spec)

Inspection of `artifacts/screenshots/` establishes the verified design language:

### 1. Header & Top App Bar

- **Observed:**
  - Left: Lowercase brand wordmark `"albo."` in bold terracotta (`#7a2b02`), followed by the screen title (e.g. `"Home"`, `"Collections"`, `"Search"`, `"Settings"`) in medium-weight text (`#1c1b1a`).
  - Right: Circular icon buttons (search magnifier in light circle, user avatar profile icon filled with dark terracotta).
- **Detail Screen Top Bar:** Back arrow circle button on left, title `"Collection Details"` (or Save Details), and profile circle on right.

### 2. Navigation & Actions

- **Bottom Navigation Bar:**
  - Docked bottom bar with warm background (`#fcf8f6`) and 4 tabs:
    1. **Home** (`ic_home`)
    2. **Collections** (`ic_folder`)
    3. **Search** (`ic_search`)
    4. **Settings** (`ic_settings`)
  - **Active Tab Indicator:** Fully rounded pill-container (`pill`, ~56x36px) filled with bright amber/marigold (`#f7931e`), with dark/white icon inside. Inactive icons are unadorned outlines in dark brown/black.
- **Floating Action Buttons (FABs):**
  - Extended FAB on Home Screen: `"＋ Save Item"` in dark terracotta pill container (`#7a2b02`).
  - Circular FAB on Collections Screen: `＋` icon in dark terracotta circle (`#7a2b02`).

### 3. Filter Chips & Segments

- **Horizontal Scrolling Chips:**
  - Selected state: Deep terracotta (`#7a2b02`) pill with white text and item count in parentheses (e.g. `"All (5)"`, `"All Items"`).
  - Unselected state: Soft warm grey/beige (`#ede6e1`) pill with dark text.

### 4. Cards & Content Tiles

- **Two-Column Grid (Collections):**
  - Generously rounded cards (`rounded: 20px` / `card`) on light neutral container (`#f7f3f1`).
  - Upper half: Photo thumbnail with small circular category icon badge pinned to top-right.
  - Lower half: Bold collection title (e.g. `"Recipes"`) and item count (`"42 items"`).
- **Horizontal Carousel ("Recently Saved" on Home):**
  - Rounded cards (~170px width) with category chip (e.g. `"Article"`, `"Recipe"`) overlaid on top-left of photo.
  - Source domain (`medium.com`, `seriouseats.com`) in small muted text.
  - Title and date (`"Today"`, `"Yesterday"`).
- **Feed Rows ("Your Collection Feed" on Home):**
  - Rounded rectangular container with circular thumbnail avatar on left.
  - Right side: Domain, colored category pill badge, title (2 lines max), timestamp (`"Saved Oct 22"`), and quick-action icon buttons (share, bookmark).

### 5. Detail Screen ("Collection Details")

- **Hero Image Card:** Rounded rectangle with overlaid badges (`Article & Recipe`, favorite heart outline) and title + URL on dark gradient bottom overlay.
- **Action Strip:** `"Visit Original Source"` with launch icon inside a soft rounded pill tile with chevron.
- **Metadata Card:** 2x2 grid displaying `Added Date`, `Estimated Time`, `Content Type`, and `Word Count` inside soft circular/oval pill tiles.
- **Notes Card:** Quotation badge, note text, and `"Edit"` button.
- **Collections Assignment Card:** Rows showing collections assigned to the item, with leading circular icons and chevrons.
- **Bottom Action Dock:** Floating dock with 4 pill buttons:
  - `Edit` (pencil)
  - `Move` (arrow box)
  - `Share` (share node)
  - `Delete` (trash can, soft light red/pink container `#fcdcd9` with red text/icon `#d32f2f`).

### 6. Settings Screen

- Grouped card sections:
  - **Storage & Cache:** Storage usage progress bar, breakdown tags (`Docs`, `Media`, `Data`), and dark terracotta `"Clear Cache"` pill button.
  - **Appearance:** Theme mode selector with 3 segmented tiles (`System`, `Light`, `Dark`). Selected state has dark terracotta background.
  - **Connectivity & Sync:** `Offline Sync` toggle switch and `Sync over Wi-Fi only` checkbox.
  - **About:** Version info (`Albo Mobile Version 4.8.2-stable`) and license link.

---

## Verified Design Tokens Summary

| Token                    | Observed Value | Usage                                                   |
| :----------------------- | :------------- | :------------------------------------------------------ |
| `primary`                | `#7A2B02`      | Active chips, primary FAB, action pill buttons          |
| `secondary`              | `#F7931E`      | Active bottom navigation pill indicator                 |
| `surface`                | `#FCF8F6`      | Screen background, top app bar                          |
| `surface-container`      | `#F4EEEA`      | Card containers, input fields, chip inactive background |
| `surface-container-high` | `#EDE6E1`      | Inner badges, subtle pill groupings                     |
| `on-surface`             | `#1C1B1A`      | Headings, primary text                                  |
| `on-surface-variant`     | `#6B5851`      | Subtitles, domains, secondary metadata                  |
| `badge-article`          | `#F39200`      | Orange tag for Articles                                 |
| `badge-recipe`           | `#FAE8DC`      | Warm peach tag for Recipes                              |
| `badge-video`            | `#F7DED5`      | Soft rose/peach tag for Videos                          |
| `delete-container`       | `#FCDCD9`      | Delete button background                                |
| `delete-tint`            | `#D32F2F`      | Delete button icon and text                             |
| `corner-radius-card`     | `20dp`         | Main cards, hero previews, group sections               |
| `corner-radius-pill`     | `9999dp`       | Chips, FABs, bottom dock action buttons, search bar     |

---

## Status & Unclear Details (TBD)

- **Financial / Goal Screens (Google Stitch reference):** `search_discovery_screen.png` displays queries for "Tokyo Trip Q3 Savings", "Emergency Safety Cushion (82% completed)", and transaction search ("-$6.50 Paid via Albo..."). This indicates Stitch or another reference mock may have blended finance/budget tracking with content saving. **Status: TBD** — Clarification needed on whether financial goal tracking is in scope or if Albo Android should strictly remain a content/link/place saver per `artifacts/albo-appstore.md`.
- **Dynamic Theming / Dark Mode:** Exact dark mode hex tokens are not shown in screenshots; standard inverted dark tones will be derived when dark mode is implemented.
