# Shohoj Biye — Improvement Analysis & Project Blueprint

**Digital Muslim Marriage Registration & Smart Kabin Calculation System**
Native Android App — Kotlin, XML, Material Design 3, MVVM, Room — no Firebase

---

## 1. Project Snapshot

| | |
|---|---|
| Platform | Native Android app (Kotlin, XML, Material Design 3, MVVM, Room), backed by a custom server — **no Firebase** |
| Positioning | Bangladesh's first smart Muslim marriage registration + Kabin calculation platform |
| Problem addressed | Manual paperwork, inconsistent/unrealistic Kabin amounts, no way to verify registrations digitally, lost or damaged paper Kabinnama |
| Roles | General User, Kazi (registrar), Admin |
| Stats (original concept) | 15+ features · 5 DB tables · 10 screens · 79 files |
| Stack (as originally presented) | Kotlin, XML + Material Design 3, MVVM, Room (SQLite), Firebase (Auth/Firestore/Storage/FCM), ZXing (QR), iText7 (PDF), Glide |
| Stack (recommended, no Firebase) | See §3 |

The core idea is strong and the feature list is genuinely comprehensive for a student project — the gaps below are less "missing ideas" and more "what a reviewer, a real Kazi, or a production rollout would push back on."

---

## 2. Where This Needs to Get Stronger

### A. Legal & religious validity — the biggest gap
Marriage registration in Bangladesh legally runs through a government-licensed Kazi under the Muslim Marriages and Divorces (Registration) Act, 1974. Nothing in the deck explains how a "Digital Kabinnama" acquires the same legal standing as an official one.
- Treat **National Integration with the Kazi/Marriage Directorate** as a day-one conversation, not roadmap item #2. Until that exists, position the app explicitly as *a Kazi's digital assistant / pre-registration tool*, not a replacement for the official registry.
- The Kabin (Mahr) amount is a mutual, negotiated agreement in Islamic jurisprudence, not something an algorithm should output as a fixed figure. Reframe "Smart Kabin Calculator" as a **guidance/reference estimator** with a visible disclaimer, and have the Islamic-guidance content reviewed by a qualified Alim — not only supervisor-approved as a CS deliverable. This protects both credibility and users.

### B. Backend & data architecture
- Even with a single Android client, a backend isn't optional here — Kazi dashboards, admin oversight, fraud detection, and duplicate-registration checks all need a central, always-on server that outlives any one device. Fraud scoring, duplicate detection, and Kabin tiering must live there, never on-device, or a modified APK can bypass every check.
- **Duplicate NID / multiple-marriage detection** can still only see records inside your own database — it can't catch a marriage registered elsewhere. Flag this as a known limitation until real NID-API access lands (already on your roadmap); a manual Kazi attestation step is a reasonable stopgap.
- **5 DB tables is light** for this feature set. Users, profiles, registrations, Kabin records, Kazi accounts, fraud logs, and audit trails alone push this to 8–10 tables — worth an ER-diagram pass before the viva (see §4). A relational DB (PostgreSQL) suits this better than Firestore, given how relational these entities are and how much audit-trail integrity matters here.

### C. Security & privacy
- NID numbers and income data are sensitive PII. Storing them as plaintext is a real risk — encrypt NID at rest (column-level encryption or a KMS-backed encryption service in Postgres) and enforce access with proper server-side authorization checks, not app-level role checks alone.
- A documented data-retention/deletion policy is worth having on paper — Bangladesh's Digital Security Act and the draft Data Protection Act will matter if this ever goes past a thesis. Auth itself is intentionally kept minimal here (see §2H) since this is a demo build, not a production login system.
- The 0–100 fraud score needs an appeals path. A false positive silently blocking someone's marriage registration is a serious failure mode, not a minor bug.

### D. Payments
Registration already captures "Instalment, Payment Method" for Kabin, but no payment gateway is in the stack. **bKash / Nagad / Rocket** integration — Bangladesh's dominant mobile financial services — is a natural, high-value addition given the domain, and an easy "why didn't you add this" question to pre-empt.

### E. Localization & accessibility
- The deck (and likely the UI) is in English, but the real users — Kazis, parents, rural families — mostly operate in Bengali. Bengali-first UI belongs in the base product, not filed under the "voice assistant" roadmap item.
- Larger touch targets, simpler flows, and Bengali text matter for less digitally-literate users — often older family members or Kazis in rural union parishads, who are arguably the primary users of this app.

### F. Engineering practices
- No testing strategy is mentioned — expect examiners to ask, and it matters more than usual given the app touches legal documents and money. Unit tests on the Kabin/fraud logic (backend) and ViewModel/Repository tests (app) are the highest-value ones to have.
- Add Hilt for dependency injection — it keeps ViewModels and Repositories testable and is a low-effort, high-credibility addition for a defense. Set up basic CI (lint + test on push) for both the app and backend.

### G. The presentation deck itself
- Add an actual **ERD / architecture diagram** slide instead of text-only layer descriptions — reviewers respond far better to a visual than a bulleted stack list.
- Add 2–3 real **app screenshots or mockups** — an all-icon, no-UI deck under-sells a working app.
- Add a **"Limitations & Future Work"** slide, distinct from the roadmap slide — examiners expect this explicitly in a viva.
- Back the "Bangladesh's first" claim with a one-line comparison against existing manual/digital alternatives — or be ready for that question if there's genuinely no prior art.

### H. What replaces Firebase, now that it's out
Dropping Firebase removes three things you were getting for free — each needs a real stand-in:
- **Auth**: kept deliberately basic, since this is a demo build rather than a production system — plain email/phone + password, hashed with bcrypt, and a single JWT issued on login (no OTP step, no OAuth provider, no separate refresh-token flow). The app sends that JWT as a Bearer token on every request; the backend checks it and reads the user's role off it. It's enough to demonstrate sign-up, sign-in, and role-based screens (User/Kazi/Admin) without building out infrastructure that isn't the point of the project.
- **Storage**: an S3-compatible object store (AWS S3, DigitalOcean Spaces, or self-hosted MinIO) for photos and Kabinnama PDFs. Backend issues short-lived pre-signed upload/download URLs to the app rather than storing binaries in Postgres.
- **Push notifications**: the real trade-off. Without Firebase, native Android push means either self-hosting a push relay (UnifiedPush + ntfy, no Google dependency) or, more realistically for a first build, skipping real push and relying on in-app notifications + periodic background sync via WorkManager. I'd pick the latter for now — standing up push infrastructure from scratch isn't worth it before the core product is proven.

---

## 3. Recommended Architecture (Native Android + Backend, No Firebase)

```
Android App — Kotlin
├─ XML layouts, Material Design 3, Navigation Component
├─ MVVM — ViewModel, LiveData/StateFlow, Coroutines
├─ Hilt — dependency injection
├─ Room — local offline cache via Repository pattern, NOT the source of truth
├─ Retrofit + OkHttp — talks to the backend API, handles token attach/refresh
├─ WorkManager — background sync, and stands in for push for now (see §2H)
└─ EncryptedSharedPreferences / Android Keystore — token + local data protection

Backend — single source of truth
├─ API layer — versioned REST, OpenAPI-documented
├─ Node.js/Express — Kabin rules, fraud policy, registration workflow
├─ PostgreSQL (Prisma/TypeORM) — relational core data, column-encrypted NID
├─ Auth — plain email/password (bcrypt), single JWT on login, no OTP or OAuth
├─ Storage — S3-compatible (photos, Kabinnama PDFs) via pre-signed URLs
└─ QR + PDF generation — `qrcode` + a PDF library, generated server-side so every Kabinnama is consistent and reproducible if you add a Kazi web console later
```
Room earns its keep here as an offline cache rather than a data store of record — genuinely useful given rural Kazi offices won't always have solid connectivity, syncing back to the backend via the API once it returns. Keep the backend as a clean, versioned API even for a single client — it's what lets you add a Kazi admin console or any second client later without a rebuild, and it's the only place fraud/duplicate logic can be trusted.

---

## 4. Suggested Data Model (Core Entities)

Relational (PostgreSQL) — a natural fit now that a proper backend exists by default.

| Table | Key Fields | Note |
|---|---|---|
| `users` | id, phone, email, password_hash, role | |
| `profiles` | user_id, name, NID (encrypted), photo, education, job_type, income | Split from `users` — PII isolation |
| `registrations` | id, groom_id, bride_id, kazi_id, date, venue, status, reg_number, qr_code | |
| `kabin_records` | registration_id, tier, amount, instalment_plan, payment_method, risk_flag | |
| `kazi_accounts` | user_id, license_no, jurisdiction, verified_status | |
| `fraud_logs` | registration_id, risk_score, reason_codes, reviewed_by, appeal_status | NEW |
| `payments` | kabin_record_id, gateway, txn_id, amount, status | NEW |
| `audit_trail` | entity_id, action, actor_id, timestamp | NEW — needed for legal defensibility |

---

## 5. Prioritized Roadmap

**Now (Phase 1 — backend + Android app together)**
- Build the backend API first: auth, registration workflow, Kabin rules, fraud logic — all server-side, versioned
- Build the Android app (Kotlin/M3/MVVM/Room/Hilt) against that same API
- Plain email/password auth (single JWT, no OTP/OAuth), S3-compatible storage, encrypt NID at rest — no Firebase anywhere
- Bengali localization
- Reframe Kabin Calculator with disclaimer + Alim-reviewed guidance content
- In-app notifications + WorkManager sync in place of push for now (see §2H)

**Next**
- bKash/Nagad payment integration
- Expand schema (fraud_logs, payments, audit_trail)
- Test suites + CI for both the app and backend

**Later**
- National integration with Kazi Directorate
- NID/biometric API verification
- Real push (UnifiedPush/ntfy) if in-app notifications prove insufficient
- AI matchmaking · Blockchain verification · Bengali voice assistant

National integration is still the real unlock — most other items compound in value once that exists.

---

## 6. One-Line Takeaway

A single, well-organized Android app against a clean custom backend is the right shape for this project — the backend is what makes fraud checks, Kazi oversight, and future integrations trustworthy, not an extra layer. Keep pushing on: (1) a real answer for legal validity before calling this a "registration" system, (2) Bengali as the default language, and (3) server-side enforcement of every rule that touches money or fraud, so the app itself can never be the thing bypassing them.
