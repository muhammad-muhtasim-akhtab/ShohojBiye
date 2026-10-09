# Implementation Plan - Fix Bugs and Refine Authentication/Registration

This plan addresses several bugs and logical errors identified in the ShohojBiye app, particularly in the authentication, registration, and calculator modules.

## User Review Required

> [!IMPORTANT]
> The role selection is moved from Login to Signup. Users will no longer select their role when logging in; the role will be retrieved from the server based on their credentials.
> Passwords are being added to the authentication flow. This is a breaking change for any existing mock accounts.

## Proposed Changes

### 1. Authentication & Session Management

#### [MODIFY] [AuthModels.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/data/remote/model/AuthModels.kt)
- Add `password` field to `LoginRequest` and `RegisterRequest`.
- Ensure `LoginResponse` includes `role` and `userId`.

#### [MODIFY] [SessionManager.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/util/SessionManager.kt)
- Add methods to save and retrieve `userId` and `userRole`.

#### [MODIFY] [UserRepository.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/data/repository/UserRepository.kt)
- Update `login` and `register` methods to handle passwords and save role/userId to `SessionManager`.

#### [MODIFY] [AuthViewModel.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/auth/AuthViewModel.kt)
- Update `login` to use password and remove manual role parameter.
- Add `signup` method.

#### [MODIFY] [LoginScreen.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/auth/LoginScreen.kt)
- Add Password field.
- Remove Role selector.
- Link "Sign up" button to the signup screen.

#### [NEW] [SignupScreen.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/auth/SignupScreen.kt)
- Implement Signup UI with Name, Email, Phone, Password, and Role selection (User/Kazi).

#### [MODIFY] [ShohojBiyeNavGraph.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/navigation/ShohojBiyeNavGraph.kt)
- Add `Screen.Signup` route.
- Initialize `userRole` from `SessionManager`.

#### [MODIFY] [Screen.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/Screen.kt)
- Add `Signup` screen destination.

### 2. Marriage Registration

#### [MODIFY] [RegistrationFormScreen.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/registration/RegistrationFormScreen.kt)
- Remove hardcoded Kazi ID; fetch it from `SessionManager` or `UserRepository`.
- Implement a Date Picker for the marriage date.
- Add basic field validation.

### 3. Kabin Calculator

#### [MODIFY] [CalculatorScreen.kt](file:///home/kali/AndroidStudioProjects/ShohojBiye/app/src/main/java/com/example/shohojbiye/ui/calculator/CalculatorScreen.kt)
- Add UI elements to select Education and Social status instead of using hardcoded mock values.

## Verification Plan

### Automated Tests
- I will attempt to run existing unit tests if applicable.
- I will manually verify the flow using the emulator/device if possible.

### Manual Verification
- Deploy the app.
- Test Signup with different roles.
- Test Login with correct/incorrect credentials.
- Verify Dashboard content based on the logged-in role.
- Test Marriage Registration and check if it correctly uses the logged-in Kazi's ID.
- Test Kabin Calculator with various inputs.
