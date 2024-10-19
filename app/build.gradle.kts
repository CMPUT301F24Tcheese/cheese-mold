plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Ensure Google services plugin is applied
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Firebase BOM - Manages consistent versions for all Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // Firebase Firestore and Realtime Database
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")

    // AndroidX Libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")

    // Retain original library references
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Ensure Google services plugin is applied at the app level
apply(plugin = "com.google.gms.google-services")
