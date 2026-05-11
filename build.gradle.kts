// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // ✅ Hilt plugin должен совпадать с версией в libs.versions.toml
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    // ✅ KSP для Kotlin 2.0.21:
    id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
}