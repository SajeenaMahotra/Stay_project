plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    buildFeatures{
        viewBinding=true
    }
    namespace = "com.example.project_stay"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.project_stay"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.junit.ktx)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)

    // Instrumented Testing
    androidTestImplementation(libs.androidx.junit)

    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.fragment:fragment-ktx:1.6.0")
    implementation("com.cloudinary:cloudinary-android:2.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("com.squareup.picasso:picasso:2.8")
    androidTestImplementation(libs.espresso.core) // Ensure this points to the correct version in your libs
    androidTestImplementation(libs.espresso.intents) // Ensure this points to the correct version in your libs
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules) // Required for running instrumented tests
    androidTestImplementation(libs.mockito.android) // Mockito for Android Tests
}


