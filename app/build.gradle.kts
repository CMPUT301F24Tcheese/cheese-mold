plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Ensure Google services plugin is applied
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase BOM - Manages consistent versions for all Firebase libraries
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))


    //New firebase
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // Old firebase
    //implementation("com.google.firebase:firebase-firestore")
    //implementation("com.google.firebase:firebase-auth")
    //implementation("com.google.firebase:firebase-storage")
    //implementation("com.google.firebase:firebase-database")


    // Glide library for profile picture
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    //ZXing libraries for QR code
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")


    implementation ("com.google.android.material:material:1.12.0") // Use the latest version available


    // AndroidX Libraries
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")


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
