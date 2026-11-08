# Albo Android XAPK Reverse Engineering Report

## 1. Package Overview

- **Source Artifact:** `artifacts/app/albo_save_and_read.xapk` (Size: ~230.3 MB).
- **Target App Name:** Albo: Save & Organize
- **Package Name (Application ID):** `com.thesortdapp.sortd`
- **Internal / Legacy Brand Name:** **Sortd** (`com.thesortdapp.sortd`, `https://auth.thesortdapp.com`)
- **Version Code:** `1017`
- **Version Name:** `1.25.5`
- **Target SDK:** 36 | **Min SDK:** 32 (Android 12L+)
- **Architecture Splits:** `base.apk`, `config.arm64_v8a.apk`, `config.en.apk`, `config.fr.apk`, `config.hdpi.apk`

---

## 2. Core Framework & Architecture

- **Tech Stack:** **Flutter / Dart** compiled to native ARM64 (`libapp.so` ~83 MB, `libflutter.so` ~11.5 MB).
- **Architecture Pattern:** **Bloc / Cubit** (evident from symbols `AuthCubit`, `VibeSearchSessionCubit`).
- **Typography / Bundled Fonts:**
  - `Sentient` (weights 200–700 with italics)
  - `AppleGaramond` (weights 300–700 with italics)
  - `Caveat` (weights 400, 600, 700)
  - `NotoNaskhArabic`, `NotoSansArabic`
  - `JetBrainsMono` (code blocks via `gpt_markdown`)
  - `KaTeX` (mathematical expressions)

---

## 3. Backend & Cloud Infrastructure

Extracted directly from the embedded production `.env` configuration file in `assets/flutter_assets/.env`:

| Service                        | Target URL / ID                                   | Purpose                                    |
| :----------------------------- | :------------------------------------------------ | :----------------------------------------- |
| **Primary Backend (Supabase)** | `https://auth.thesortdapp.com`                    | User authentication, database, and storage |
| **Dev Backend (Supabase)**     | `https://xtkxzyebjdkklkkdjdti.supabase.co`        | Development / staging environment          |
| **Product Analytics**          | PostHog (`https://eu.i.posthog.com`)              | Event tracking & analytics                 |
| **Mapping Engine**             | Mapbox (`mapbox-maps.so`, `mapbox-common.so`)     | Interactive travel places & maps           |
| **Subscriptions & Billing**    | RevenueCat                                        | In-app purchases & premium subscriptions   |
| **Bot Protection / Captcha**   | Cloudflare Turnstile (`0x4AAAAAACvwEzhRBY6ZWwDN`) | Auth bot protection                        |
| **Mobile Attribution**         | Adjust (`xc16tnhegutc`)                           | Campaign & install tracking                |
| **Identity & Login**           | Google Sign-In & Facebook SDK                     | OAuth social sign-in                       |
| **Static CDN**                 | `https://cdn.albo.inc/file/albo-static-assets/`   | Onboarding demo videos and assets          |

---

## 4. Key Android Components & Features

### Activities & Services

- **Main Launch Activity:** `com.thesortdapp.sortd.MainActivity`
- **System Share Receiver:** `com.thesortdapp.sortd.ShareDialogActivity` (handles `android.intent.action.SEND` & `SEND_MULTIPLE`)
- **Video Picture-in-Picture:** `com.thesortdapp.sortd.VideoPipActivity`
- **Quick Settings Tiles:**
  - `com.thesortdapp.sortd.AddToAlboTileService` (quick save tile)
  - `com.thesortdapp.sortd.AlboMapTileService` (quick map tile)
  - `com.thesortdapp.sortd.SuggestTileService`
- **Home Screen App Widgets:**
  - `NearbyWidgetReceiver` (shows saved places near user location)
  - `PhotoCollectionWidgetReceiver` (photo carousel / collection widget)
  - `QuickSearchWidgetReceiver` (instant search widget)
  - `DeciderPromptWidgetReceiver`

### AI & Classification

- Employs **Google ML Kit** (`mlkitinitprovider`, `liblanguage_id_l2c_jni.so`, `libtranslate_jni.so`, `libbarhopper_v3.so` for barcode/QR scanning).
- Uses `"VibeSearch"` (`VibeSearchSessionCubit`) for conversational / semantic searching across saves.
- Uses `gpt_markdown` package for rendering formatted AI notes and responses.

---

## 5. Implications for Laterly (Our Independent Android Alternative)

1. **Decoupled Architecture Confirmed:** Albo depends entirely on cloud Supabase, PostHog, RevenueCat, and Mapbox tokens tied to `thesortdapp.com`. Laterly's local-first architecture (Room + on-device OpenGraph parsing) avoids all cloud service fees, authentication walls, and external tracking while preserving user privacy.
2. **Key Feature Parity Opportunities:**
   - **Share Dialog:** Albo uses a dedicated `ShareDialogActivity` for incoming shares—identical to Laterly's `ShareReceiverActivity`.
   - **Quick Settings Tile:** Adding an Android `TileService` to Laterly for 1-tap quick saving from the system notification shade.
   - **Home Screen Widgets:** Implementing Glance / AppWidgetProvider for quick search and recent saves.
   - **Places & Maps:** Use MapLibre / OpenStreetMap for saved location viewing without paid Mapbox API tokens.
