import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    kotlin("kapt")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "dev.pinkroom.marketsight"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.pinkroom.marketsight"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val keystoreFile = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        val alpacaStreamUrl = properties.getProperty("ALPACA_STREAM_URL") ?: ""
        val alpacaApiId = properties.getProperty("ALPACA_API_ID") ?: ""
        val alpacaApiSecret = properties.getProperty("ALPACA_API_SECRET") ?: ""

        buildConfigField(type = "String", name = "ALPACA_STREAM_URL", value = alpacaStreamUrl)
        buildConfigField(type = "String", name = "ALPACA_API_ID", value = alpacaApiId)
        buildConfigField(type = "String", name = "ALPACA_API_SECRET", value = alpacaApiSecret)
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
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // CORE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // COMPOSE
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // RETROFIT
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.gson)

    // OkHTTP
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.interceptor)

    // SCARLET
    implementation(libs.scarlet.core)
    implementation(libs.scarlet.stream)
    implementation(libs.scarlet.gson)
    implementation(libs.scarlet.okhttp)

    // DAGGER HILT
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation)

    // SPLASH
    implementation(libs.splash)

    // UNIT TEST
    testImplementation(libs.junit)
    testImplementation(libs.junit.api)

    // END-TO-END TEST
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // DEBUG
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}