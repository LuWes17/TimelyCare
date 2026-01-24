# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

### Building the App
```bash
# Build the main app module
./gradlew build

# Build just the app variant
./gradlew app:build

# Build with Wear OS module
./gradlew :app:build :wear:build
```

### Cleaning Build Files
```bash
# Clean build artifacts
./gradlew clean

# Clean and rebuild
./gradlew clean build
```

### Running the App
```bash
# Install and run on connected device/emulator
./gradlew installDebug

# Run directly
./gradlew :app:assembleDebug

# Build release version
./gradlew :app:bundleRelease
```

### Gradle Sync
```bash
# Sync Gradle files
./gradlew sync
```

## Code Architecture

### High-Level Overview
TimelyCare is an Android medication management app built with **Jetpack Compose** and **MVVM pattern** with **StateFlow** for reactive state management. The app targets older adults with accessibility-focused UI design.

### Project Structure
- **app/** - Main Android application module
- **wear/** - Wear OS companion app
- **Shared components** - Between phone and watch via Wear OS data sync

### Key Architecture Patterns

#### 1. Repository Pattern (Data Layer)
All data access is abstracted through repository singletons:
- **MedicationRepository** (`data/MedicationRepository.kt`) - Manages medication list using `StateFlow<List<Medication>>`
- **MedicationTakenRepository** - Tracks which medications were taken
- **SettingsRepository** - Manages user preferences via SharedPreferences
- **EmergencyContactRepository** - Manages emergency contacts
- Health data repositories - Track HeartRateData, BloodPressureData, GlucoseData

All repositories follow singleton pattern: `getInstance(context)` with lazy initialization and thread-safe double-checked locking.

#### 2. State Management
- Uses **StateFlow** from kotlinx.coroutines for reactive state
- Repositories expose public `StateFlow` properties that UI observes
- Data changes automatically trigger recomposition in Compose
- Example: `MedicationRepository.medications: StateFlow<List<Medication>>`

#### 3. UI Layer (Jetpack Compose)
- **Navigation** - Single-screen app navigation via composable state in `TimelyCareApp()` (`ui/navigation/TimelyCareNavigation.kt`)
- **Screens** - Located in `ui/screens/` organized by feature (medications, dashboard, calendar, contacts, health metrics)
- **Components** - Reusable UI components in `ui/components/` (headers, bottom navigation, cards)
- **Theme** - Material 3 theming with 6 color variants and dark mode support (`ui/theme/`)

#### 4. Data Models
Core data classes in `data/`:
- **Medication** - Represents a medication with type, frequency, times, start/end dates, special instructions
- **Frequency** - Sealed class (Daily or SpecificDays with DayOfWeek set)
- **MedicationType** - Enum (PILL, TABLET, CAPSULE, LIQUID, INJECTION, OTHERS)
- Health metrics: **HeartRateData**, **BloodPressureData**, **GlucoseData**
- **EmergencyContact** - Contact information with phone numbers

#### 5. Wear OS Integration
- **MedicationDataService** (`service/MedicationDataService.kt`) - Syncs medication data to Wear OS
- Uses Google Play Services Wearable API
- Syncs medications and medication taken records when data changes
- Watch receives updates via DataClient without needing a return trip

#### 6. Settings & Localization
- **SettingsRepository** uses SharedPreferences to persist user preferences
- **LocaleHelper** (`utils/LocaleHelper.kt`) - Handles language switching (English, Filipino)
- Settings include: language, theme color (6 options), dark mode, alert level (LOW/NORMAL/HIGH)

### Important Implementation Details

#### Medication Scheduling
- Medications have `medicationTimes: List<LocalTime>` - can have multiple times per day
- Frequency determines which days: Daily or specific day-of-week set
- Start/end dates define medication validity period
- Dashboard filters medications for the current date

#### Composable Organization
- Large composables are split into logical components
- Headers and navigation bars are centralized in `ui/components/`
- Each screen is self-contained and can be composed independently
- Preview functions available for composables in the same file

#### Theme System
- Material 3 with 6 custom color palettes
- Dark mode support via UserSettings
- Dynamic theme switching updates all screens via SettingsRepository StateFlow
- Accessibility-focused: large text sizes, high contrast colors

## Development Workflow

### Modifying the Data Layer
1. Data models should remain immutable (data classes)
2. When adding new data: create a repository with StateFlow exposure
3. Use lazy initialization for repositories with context
4. Sync changes to Wear OS if needed via MedicationDataService

### Adding New Features
1. Create a new screen composable in `ui/screens/[feature]/`
2. Add navigation state to `TimelyCareApp()` in navigation/TimelyCareNavigation.kt
3. Create a header component if needed in `ui/components/[FeatureName]Header.kt`
4. Use existing repositories for data access
5. Update bottom navigation if it's a main tab

### Testing Considerations
- No existing test suite; focus on manual testing in emulator
- Test on both phone (app) and Wear OS (wear module)
- Verify data syncs between phone and watch
- Check accessibility: large touch targets, readable fonts, logical tab order

## Dependencies & Libraries

**Core Framework:**
- Jetpack Compose (androidx.compose)
- Kotlin Coroutines (kotlinx.coroutines)
- AndroidX Core, Lifecycle, Activity

**Wear OS:**
- Google Play Services Wearable (`com.google.android.gms:play-services-wearable`)
- Wear OS Jetpack libraries

**Material Design:**
- Material 3 (androidx.material3)

**Testing:**
- JUnit, AndroidX Test, Espresso

## Build Configuration

**Kotlin/Android Configuration:**
- Compile SDK: 36
- Min SDK: 26 (Android 8.0)
- Target SDK: 36 (Android 15)
- Java Version: 11
- Kotlin Compose Compiler enabled

**Plugin Management:**
- Uses Gradle version catalog (libs.*)
- Repositories: Google, Maven Central, Gradle Plugin Portal

## File Organization Reference

```
app/src/main/java/com/example/timelycare/
├── MainActivity.kt                    # Entry point, theme setup
├── data/                              # Data models & repositories
│   ├── Medication.kt                  # Core data class
│   ├── MedicationRepository.kt        # Medication CRUD + Wear sync
│   ├── MedicationTakenRepository.kt   # Track taken medications
│   ├── SettingsRepository.kt          # User preferences
│   ├── EmergencyContactRepository.kt  # Emergency contacts
│   ├── HealthMetrics.kt               # Health data models
│   ├── HeartRateData.kt
│   ├── BloodPressureData.kt
│   └── GlucoseData.kt
├── service/
│   └── MedicationDataService.kt       # Wear OS sync logic
├── ui/
│   ├── navigation/
│   │   └── TimelyCareNavigation.kt    # App-level navigation & state
│   ├── screens/
│   │   ├── dashboard/                 # Daily medication view
│   │   ├── medications/               # Medication list & CRUD
│   │   ├── calendar/                  # Medication calendar view
│   │   ├── contacts/                  # Emergency contacts
│   │   ├── heartrate/                 # Heart rate metrics
│   │   ├── bloodpressure/             # Blood pressure metrics
│   │   ├── glucose/                   # Glucose metrics
│   │   └── settings/                  # User settings
│   ├── components/                    # Reusable UI components
│   └── theme/
│       ├── Color.kt                   # 6 color themes
│       ├── Theme.kt                   # Material 3 theming
│       └── Type.kt                    # Typography
└── utils/
    └── LocaleHelper.kt                # Language switching
```

## Common Development Tasks

### Running Tests
No automated tests currently exist. Testing is manual:
- Install app on emulator: `./gradlew installDebug`
- Run app: `./gradlew :app:run` or use Android Studio's Run button

### Adding a New Medication Field
1. Update `Medication` data class in `data/Medication.kt`
2. Update `AddEditMedicationScreen` form in `ui/screens/medications/AddEditMedicationScreen.kt`
3. Update display components (e.g., `MedicationCard`)
4. Sync to watch if needed via `MedicationDataService`

### Debugging
- Logcat in Android Studio shows app logs
- Use `Log.d()` for debug messages
- Check Wear OS sync logs: `MedicationSync` tag in Logcat
- Enable emulator's "Show Logs" in extended controls

## Gradle Alias Resolution

Dependencies use Gradle version catalog. Key libraries referenced via:
- `libs.androidx.*` - Android X libraries
- `libs.compose.*` - Jetpack Compose
- `libs.play.services.wearable` - Wear OS

These are defined in `gradle/libs.versions.toml` (if present) or similar configuration file.
