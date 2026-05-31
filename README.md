# UrlCleanerEngine

UrlCleanerEngine is a lightweight Kotlin-based library for Android designed to sanitize URLs by removing tracking parameters and identifying the target native application for routing.

The engine utilizes a decoupled architecture where cleaning logic is separated from filtering rules, allowing for efficient updates and community-driven expansion of supported platforms.

## Installation

### 1. Configure Repositories
Add the JitPack repository to your `settings.gradle.kts` file:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2. Add Dependency
Add the following dependency to your module-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.matiasvidal56:UrlCleanerEngine:v1.1.0")
}
```

## Implementation

### Initialization
Initialize the engine once in your Activity or Application class to load the rules from assets:

```kotlin
UrlCleaner.init(context)
```

### URL Sanitization & Routing
The `clean` method returns a `CleanResult` object containing the sanitized URL and a list of preferred Android package names.

```kotlin
val rawUrl = "https://www.instagram.com/reels/C123/?igsh=abc123"
val result = UrlCleaner.clean(rawUrl)

// Access sanitized URL
val cleanUrl = result.url 
// Output: https://www.instagram.com/reels/C123/

// Access target packages for routing
val targetPackage = result.preferredPackages.firstOrNull()
// Output: "com.instagram.android"
```

## Contribution Guide

The engine's intelligence relies on `rules.json`. We encourage the community to keep this database updated.

### JSON Structure
Rules are grouped by platform to prevent duplicates. Each entry supports multiple hosts and package names:

```json
{
  "name": "YouTube",
  "hosts": ["youtube.com", "youtu.be", "m.youtube.com"],
  "packages": ["com.google.android.youtube"],
  "params": ["si", "feature"]
}
```

### How to contribute
1. Navigate to `cleaner-core/src/main/assets/rules.json`.
2. Add new parameters to existing platforms or create a new platform entry.
3. Submit a Pull Request.

## License
This project is licensed under the MIT License.