plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.baseline.profile)
    alias(libs.plugins.detekt)
    alias(libs.plugins.gms)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.kroune.tiktokcopy"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "io.github.kroune.tiktokcopy"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {

    implementation(compose.runtime)
    implementation(compose.foundation)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.components.resources)

    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    api(libs.decompose)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.serialization.kotlinx.protobuf)
    implementation(libs.ktor.client.content.negotiation)

    implementation(libs.kotlinx.coroutines.core)


    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.activity.compose)
    implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.workmanager)

    implementation(libs.koin.core)
}