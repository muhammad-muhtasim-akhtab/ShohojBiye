# Implementation Plan - Project Review and Architecture Refinement

This plan addresses several bugs, security gaps, and architectural flaws identified in the ShohojBiye app, aligning it with the project's blueprint for a "Digital Muslim Marriage Registration & Smart Kabin Calculation System."

## User Review Required

> [!IMPORTANT]
> **Encryption Change**: All NID numbers stored in the database will be encrypted using AES-GCM. Existing data in the local Room database will be incompatible and should be cleared.
> **Registration Flow**: A new "Legal Disclaimer" step will be added to the registration process to emphasize that this is a digital assistant tool and not yet a replacement for official government records.

## Proposed Changes

### 1. Security & Privacy (PII Protection)

#### [MODIFY] [SecurityUtils.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/util/SecurityUtils.kt)
- Replace mock Base64 "encryption" with real AES-256-GCM encryption using `AndroidKeyStore`.

#### [MODIFY] [UserRepository.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/data/repository/UserRepository.kt)
- Encrypt NID before saving to the database/profile.
- Refactor `login` and `register` to return `Result<Unit>` for better error reporting.

#### [MODIFY] [MarriageRepository.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/data/repository/MarriageRepository.kt)
- Encrypt witness NIDs and party NIDs before storage.
- Update `registerMarriage` to return the `registrationId` from the server.

### 2. Marriage Registration Refinement

#### [MODIFY] [RegistrationViewModel.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/registration/RegistrationViewModel.kt)
- Use the `registrationId` returned by the repository instead of generating one locally.
- Implement field validation for each step.

#### [MODIFY] [RegistrationFormScreen.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/registration/RegistrationFormScreen.kt)
- Add a "Step 0" or an initial notice about the legal standing of the app.
- Add field validation before allowing the user to click "Next".

### 3. Architecture & Code Quality

#### [MODIFY] [KabinCalculator.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/domain/usecase/KabinCalculator.kt)
- Refine the disclaimer text to be more authoritative as suggested in the blueprint.

#### [MODIFY] [DashboardScreen.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/dashboard/DashboardScreen.kt)
- Ensure the UI reflects the user's role consistently and provides clear actions.

### 4. Localization

#### [MODIFY] [strings.xml](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/res/values/strings.xml) and [strings.xml (bn)](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/res/values-bn/strings.xml)
- Add missing translations for newly added UI elements and error messages.

## Verification Plan

### Automated Tests
- Run `gradle build` to ensure no compilation errors.
- Unit tests for `SecurityUtils` encryption/decryption.
- Unit tests for `FeeCalculator` and `KabinCalculator`.

### Manual Verification
- Verify Signup/Login flow.
- Verify that NIDs are stored encrypted in the database (via inspection or logs).
- Verify the multi-step registration flow and validation.
- Check the localized UI in both English and Bengali.
