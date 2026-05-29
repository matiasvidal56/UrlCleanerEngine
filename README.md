# UrlCleanerEngine

UrlCleanerEngine is a lightweight Kotlin-based library for Android designed to identify and remove tracking parameters from social media and web URLs. The engine utilizes a decoupled architecture where cleaning logic is separated from filtering rules, allowing for efficient updates and community contributions.

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
    implementation("com.github.matiasvidal56:UrlCleanerEngine:v1.0.0")
}
```

## Implementation

### Initialization
The engine requires a context to load the initial filtering rules from the assets. Initialize the engine once in your Activity or Application class:

```kotlin
UrlCleaner.init(context)
```

### URL Processing
Pass a URL string to the `clean` method to return the processed string without tracking parameters:

```kotlin
val rawUrl = "https://www.instagram.com/p/Example/?igsh=tracking_id"
val processedUrl = UrlCleaner.clean(rawUrl)
// Output: https://www.instagram.com/p/Example/
```

## Contribution Guide

The filtering rules are stored in a structured JSON format to facilitate updates without modifying the source code.

### Adding New Filters
1. Navigate to `cleaner-core/src/main/assets/rules.json`.
2. Locate the `global_trackers` array for universal parameters or `platform_rules` for host-specific parameters.
3. Submit a Pull Request with the proposed changes.

## License
This project is licensed under the MIT License.