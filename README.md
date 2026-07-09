# Life U

<p align="center">
  <img src="app/src/main/res/drawable/img_app_icon.png" width="140" alt="Life U logo" />
</p>

<p align="center">
  Intelligent student companion for studying, scheduling, finances, and AI help.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-blueviolet" />
  <img src="https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF?logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Room-Database-orange" />
  <img src="https://img.shields.io/badge/Firebase-AI-FFCA28?logo=firebase&logoColor=black" />
</p>

## What it does

Life U brings your student tools into one app:

- AI tutor chat for study help
- Tasks and weekly schedule
- GPA and grade tracking
- Budget and expense tracking
- Notes and resource vault
- Profile and theme settings

## Beautiful overview

```mermaid
flowchart TB
    A[Life U] --> B[Study]
    A --> C[Plan]
    A --> D[Money]
    A --> E[Profile]

    B --> B1[AI Tutor]
    B --> B2[Notes]
    B --> B3[Resources]

    C --> C1[Tasks]
    C --> C2[Schedule]
    C --> C3[Pomodoro Focus]

    D --> D1[Income]
    D --> D2[Expenses]
    D --> D3[Budget Tracking]

    E --> E1[Theme]
    E --> E2[University]
    E --> E3[User Info]
```

```mermaid
pie title Life U feature focus
    "Study & AI" : 35
    "Planning" : 25
    "Finances" : 20
    "Profile & Settings" : 20
```

## Tech stack

- Kotlin
- Jetpack Compose
- Material 3
- Room
- Retrofit / OkHttp
- Coil
- Firebase AI

## Run locally

1. Open the project in Android Studio.
2. Create a `.env` file in the project root.
3. Add `GEMINI_API_KEY` to `.env`.
4. Run the app on an emulator or device.

## Notes

- The app icon has been updated to the new Life U design.
- Input validation is enabled across key forms.

