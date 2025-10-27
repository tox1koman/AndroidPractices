// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.0" apply false
    kotlin("android") version "1.9.0" apply false
    alias(libs.plugins.kotlin.compose) apply false
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "2.2.20"
}