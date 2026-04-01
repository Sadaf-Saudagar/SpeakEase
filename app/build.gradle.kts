import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

// 1. Safely load local.properties using Kotlin DSL
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.speakease"
    compileSdk = 35 // Standardizing to stable API 35

    defaultConfig {
        applicationId = "com.example.speakease"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 2. Safely grab the Key from local.properties and inject it
        val apiKey = localProperties.getProperty("NVIDIA_API_KEY") ?: "\"Bearer YOUR_FALLBACK_KEY\""
        buildConfigField("String", "NVIDIA_KEY", apiKey)
    }

    buildTypes {
        release {
            isMinifyEnabled = true // 💡 Turn on ProGuard to shrink and obfuscate your APK
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // 3. Enable BuildConfig generation so Java files can access your secret key
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}