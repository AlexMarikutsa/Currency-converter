// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false

    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
    }
}