# NallaNudi — ನಲ್ಲ ನುಡಿ

> **NallaNudi** (ನಲ್ಲ ನುಡಿ, meaning *"Good Words"* in Kannada) is an Android vocabulary-learning app that teaches English words alongside their Kannada translations and explanations.

---

## 📱 Screenshots

> *(Add your screenshots here)*

---

## ✨ Features

| Feature | Description |
|---|---|
| 🌅 Word of the Day | A new English–Kannada word is highlighted every day on the home screen |
| 🔍 Search | Instantly search words by English or Kannada text |
| 📚 Subject Categories | Words organized by subject — Science, and more |
| 🃏 Flashcards | Flip-card style view to study word–meaning pairs |
| ❓ Quiz Mode | Multiple-choice quiz to test your knowledge |
| 🔤 Spelling Practice | Type the correct spelling of the Kannada/English word |
| 🏆 Challenge Mode | Timed challenge to test vocabulary under pressure |
| 📈 Progress Tracking | See how many words you've learned, viewed, and saved |
| ❤️ My List | Save favourite words for quick access |
| 🕓 Word History | Review recently viewed words |
| 🌙 Dark Mode | Toggle between light and dark themes |
| 🔔 Daily Reminders | Scheduled notifications to keep your streak alive |

---

## 🛠️ Tech Stack

- **Language:** Java
- **Platform:** Android (minSdk 24 / targetSdk 36)
- **Build System:** Gradle (Kotlin DSL)
- **Database:** Room (SQLite) — `AppDatabase`, `WordDao`
- **UI Components:** RecyclerView, CardView, Material Design 3
- **Notifications:** AlarmManager + BroadcastReceiver

---

## 🏗️ Project Structure

```
NallaNudi/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/nallanudi/nallanudi/
│           │   ├── AppDatabase.java         # Room database singleton
│           │   ├── Word.java                # Room entity (word model)
│           │   ├── WordDao.java             # Database access object
│           │   ├── DataSeeder.java          # Pre-populates words on first launch
│           │   ├── MainActivity.java        # Home screen — Word of the Day, navigation
│           │   ├── SplashActivity.java      # Launch screen
│           │   ├── SearchActivity.java      # Search words
│           │   ├── FlashcardActivity.java   # Flashcard study mode
│           │   ├── QuizActivity.java        # Multiple-choice quiz
│           │   ├── SpellingActivity.java    # Spelling practice
│           │   ├── ChallengeActivity.java   # Timed challenge
│           │   ├── ProgressActivity.java    # Learning progress stats
│           │   ├── MyListActivity.java      # Saved/favourite words
│           │   ├── WordHistoryActivity.java # Recently viewed words
│           │   ├── WordAdapter.java         # RecyclerView adapter
│           │   ├── NotificationReceiver.java  # BroadcastReceiver for reminders
│           │   └── NotificationScheduler.java # Schedules daily alarm
│           ├── res/
│           │   ├── layout/                  # XML layout files
│           │   ├── drawable/                # Icons and backgrounds
│           │   └── values/                  # Colors, strings, themes
│           └── AndroidManifest.xml
├── build.gradle.kts                         # App-level build config
├── settings.gradle.kts
└── gradle.properties
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or newer
- JDK 11+
- Android device or emulator running Android 7.0 (API 24)+

### Clone & Run

```bash
git clone https://github.com/<your-username>/NallaNudi.git
cd NallaNudi
```

1. Open the project in **Android Studio**.
2. Let Gradle sync automatically.
3. Connect a device or start an emulator.
4. Click **Run ▶** (or press `Shift + F10`).

On first launch, `DataSeeder` automatically populates the database with vocabulary words across subjects.

---

## 📦 Dependencies

```kotlin
// Room Database
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-compiler:2.6.1")

// UI
implementation("androidx.cardview:cardview:1.0.0")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("com.google.android.material:material:1.11.0")

// Core
implementation(libs.appcompat)
implementation(libs.activity)
implementation(libs.constraintlayout)
```

---

## 🔐 Permissions

| Permission | Purpose |
|---|---|
| `POST_NOTIFICATIONS` | Show daily word-reminder notifications |
| `RECEIVE_BOOT_COMPLETED` | Reschedule reminders after device reboot |
| `SCHEDULE_EXACT_ALARM` | Deliver reminders at the exact scheduled time |

---

## 🤝 Contributing

Contributions are welcome! To add new words, edit `DataSeeder.java`. To add new subjects, follow the existing pattern of inserting `Word` objects with a new subject string.

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m "Add: my feature"`)
4. Push the branch (`git push origin feature/my-feature`)
5. Open a Pull Request

---

## 📄 License

This project is open source. Add your preferred license here (e.g., MIT, Apache 2.0).

---

## 👤 Author

**Prathaviraj Karbari**
- GitHub: [@Prithviraj1810-sudo](https://github.com/Prithviraj1810-sudo)

---

> *ನಲ್ಲ ನುಡಿ — Learn a good word every day!* 🌟
