# agenden-ring-kmp
An agenda/reminder ring app built with Kotlin Multiplatform + Compose Multiplatform.

## Structure
- `shared/` — shared models: Task, DayAgenda, TaskCategory
- `composeApp/` — Compose Multiplatform ring UI
- `iosApp/` — iOS entry point

## Build
```bash
./gradlew :composeApp:assembleDebug
```

## Features
- Visual agenda ring with task segments
- Tap-to-select tasks on the ring
- Completion rate arc overlay
- Current time pointer on ring
- Categorized tasks with color coding
