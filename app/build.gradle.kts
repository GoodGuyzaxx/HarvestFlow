import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "my.id.zaxx.harvestflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "my.id.zaxx.harvestflow"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-alpah"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val keyStoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(keyStoreFile.inputStream())

        val apiKey = properties.getProperty("API_KEY") ?: ""
        buildConfigField(
            type = "String",
            name = "API_KEY",
            value = apiKey
        )
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
        mlModelBinding = true
    }
}
kapt {
    correctErrorTypes = true
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
//    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation ("com.microsoft.onnxruntime:onnxruntime-android:latest.release")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation (libs.play.services.location)

    // Firebase Realtime Database untuk monitoring
    implementation("com.google.firebase:firebase-database:21.0.0")

    // Retrofit untuk komunikasi HTTP ke server Flask
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")

    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")


}