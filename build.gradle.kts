buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.android.tools.build:gradle:8.7.2")
    }
}

plugins {
    id("com.android.application") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}