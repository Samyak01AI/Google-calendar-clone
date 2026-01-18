# Contributing to Calendar Clone

First off, thank you for considering contributing to this project! It's people like you that make the open-source community such an amazing place to learn, inspire, and create.

## 🛠 Getting Started

### Prerequisites
* **Android Studio:** Ladybug or newer (recommended).
* **JDK:** 17 or newer.
* **Android SDK:** API Level 34 (UpsideDownCake) or higher.

### Installation
1.  **Fork** the repository on GitHub.
2.  **Clone** your fork locally:
    ```bash
    git clone [https://github.com/Samyak01AI/Google-calendar-clone](https://github.com/Samyak01AI/Google-calendar-clone)
    cd calendar-clone
    ```
3.  Open the project in **Android Studio**.
4.  Let Gradle sync finish.
5.  Create a virtual device (AVD) or connect a physical device and press **Run**.

---

## 📂 Project Structure
We follow **Clean Architecture** with **MVVM**. Please ensure your code resides in the correct package:

* `data/` - Room database, Entities, and Repositories.
* `ui/` - Fragments, Activities, ViewModels, and Adapters.
    * `ui/calendar` - Logic for the month grid.
    * `ui/event` - Logic for adding/viewing events.
* `utils/` - Helper classes (Date logic, AlarmScheduler, NotificationReceiver).

---

## 🎨 Code Style & Guidelines

### Kotlin
* Use **Kotlin** for all new code.
* Follow the [Official Kotlin Style Guide](https://developer.android.com/kotlin/style-guide).
* Use `val` (immutable) wherever possible.
* Use **Coroutines** & **Flow** for asynchronous tasks (database/network).

### XML / Layouts
* Use `ConstraintLayout` for complex screens.
* IDs should use snake_case (e.g., `@+id/tv_event_title`, `@+id/btn_save`).
* Extract hardcoded strings to `strings.xml`.
* Extract repetitive styles to `styles.xml`.

### Database
* If you modify a Room Entity (`Event.kt`), remember to **increment the database version** in `AppDatabase.kt`.

---

## 🐛 Found a Bug?
If you find a bug, please create an **Issue** including:
1.  **Steps to reproduce** (e.g., 1. Open app, 2. Click Feb 14, 3. Crash).
2.  **Expected behavior**.
3.  **Actual behavior** (Screenshots or Stack Trace).

---

## 🚀 Submitting a Pull Request (PR)

1.  **Fork** the repo and create your branch from `main`.
    ```bash
    git checkout -b feature/amazing-new-feature
    # or
    git checkout -b fix/annoying-bug
    ```
2.  If you've added code that should be tested, add tests.
3.  Ensure your code builds without warnings.
4.  **Commit** your changes with a clear message.
    * Good: "Add Swipe-to-Delete functionality for events"
    * Bad: "Update files"
5.  Push to your fork and submit a **Pull Request**.

### PR Checklist
- [ ] Code compiles and runs.
- [ ] No hardcoded strings (use `strings.xml`).
- [ ] Code follows the MVVM pattern.

---

## 📜 License
By contributing, you agree that your contributions will be licensed under the project's [MIT License](LICENSE).
