plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.cresoty.catpospoint"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.cresoty.catpospoin"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "com.cresoty.catpospoin.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.transport.runtime)
    //    implementation(libs.androidx.lifecycle.runtime.compose.android)
    val compose_version = "1.6.0"
    val compose_lifecycle_version = "2.7.0"
    val datastore_version = "1.2.0"

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation(files("libs\\commons-codec-1.4.jar"))

    implementation("androidx.datastore:datastore-preferences:$datastore_version")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$compose_lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$compose_lifecycle_version")

    implementation("androidx.compose.ui:ui:$compose_version")
    // tooling support( preview 등)
    implementation("androidx.compose.ui:ui-tooling:$compose_version")

    // foundation(Border, Background, Box, Image, Scroll, shapes, animations...)
    implementation("androidx.compose.foundation:foundation:$compose_version")
    // material design
    implementation("androidx.compose.material3:material3:1.2.0-beta01")
    implementation("androidx.compose.material:material:$compose_version")
    // material design icons
    implementation("androidx.compose.material:material-icons-core:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")
    //integration with activities
    implementation("androidx.activity:activity-compose:1.8.2")
    // integration with viewModels
    // integration with observers
//    implementation("androidx.compose.runtime:runtime-rxjava2:$compose_version")
    // UI tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_version")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.github.mik3y:usb-serial-for-android:3.4.6")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.10")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.6.1")
    implementation("io.reactivex.rxjava3:rxjava:3.1.9")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.github.yalantis:ucrop:2.2.9")
    implementation("androidx.core:core-splashscreen:1.0.1")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.48")
}
