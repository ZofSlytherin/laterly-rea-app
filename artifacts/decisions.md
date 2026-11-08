# Decisions Log

Meaningful technical and product architecture decisions for Laterly (open-source Android alternative).

## 1. Platform & Language: Android (Kotlin) with Jetpack Compose

- **Decision:** Target modern Android native using Kotlin and Jetpack Compose (Single-Activity architecture).
- **Alternatives:**
  - Multiplatform / Cross-platform (Flutter, React Native): Higher abstraction overhead, unnecessary complexity for Android-first focus, suboptimal share sheet / Android intent integration.
  - Classic Android XML Views: Verbose, boilerplate-heavy, deprecated paradigm compared to Compose.
- **Reason:** Kotlin + Compose provides the lowest complexity and fastest path to a lightweight, reactive local UI with native Android Intent handling.
- **Trade-off:** Android-only codebase; no iOS parity from shared code.

## 2. Local Persistence: Room (SQLite)

- **Decision:** Use Android Jetpack Room with SQLite for local persistence of saved items, categories, tags, and collections.
- **Alternatives:**
  - Raw SQLite / SQLDelight: More manual setup and boilerplate.
  - DataStore / Key-Value: Insufficient querying, filtering, and full-text search capabilities for collections.
  - Realm / ObjectBox: Third-party proprietary runtime dependencies.
- **Reason:** First-party Jetpack library, compile-time SQL verification, reactive Flow support, zero external server dependencies.
- **Trade-off:** Schema migrations require structured definitions as data models evolve.

## 3. Metadata Extraction & Link Previews: On-Device Lightweight Parser

- **Decision:** Extract link metadata (Open Graph / Twitter Cards / HTML `<title>` & `<meta>` tags) directly on-device using a lightweight HTML parser (e.g., Jsoup or regex/streaming stream reader) over HTTP client (OkHttp / Ktor).
- **Alternatives:**
  - Cloud scraper microservice / backend API: Introduces server hosting costs, maintenance, privacy concerns, and offline failure.
  - Client-side headless WebView evaluation: High memory and CPU consumption for basic link previews.
- **Reason:** Keeps the application local-first, offline-capable where cached, fully independent, and preserves user privacy.
- **Trade-off:** Sites requiring client-side JavaScript execution or Cloudflare verification may return incomplete metadata without specialized rendering.

## 4. Deferral of Cloud Backend & Social Collaboration

- **Decision:** Strictly build a local-only MVP (save, organize, search, view). Defer multi-user synchronization, account authentication, and social collection sharing.
- **Alternatives:**
  - Provision Firebase / Supabase backend immediately: Bloats scope before core share and organization loops are proven.
- **Reason:** Albo's internal backend APIs and schemas are unknown and unverified. Starting local-first satisfies the core utility with minimum risk and zero cloud infrastructure dependency.
- **Trade-off:** No cross-device sync or collaborative sharing in MVP.

## 5. Architectural Comparison from APK Evidence (com.thesortdapp.sortd)

- **Verified Finding:** Albo is a Flutter app reliant on cloud Supabase (`auth.thesortdapp.com`), PostHog, RevenueCat, and Mapbox, with compulsory authentication.
- **Decision:** Remain fully independent from third-party cloud infrastructure. Store full cleaned readable article bodies and metadata on-device in Room SQLite.
- **Rationale:** Keeps Laterly private, offline-first, zero-subscription, and fast, eliminating account wall friction.
- **Trade-off:** No out-of-the-box cross-device cloud synchronization without user-configured storage.
