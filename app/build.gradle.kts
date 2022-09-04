plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.mikepenz.aboutlibraries.plugin")
    id("kotlinx-serialization")
}

android {
    compileSdk = 33

    val versionCode: Int by rootProject.extra
    val versionName: String by rootProject.extra

    defaultConfig {
        this.applicationId = "vegabobo.dsusideloader"
        this.versionCode = versionCode
        this.versionName = versionName

        minSdk = 29
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Temporary till we fix translations
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

dependencies {
    implementation(AndroidX.appCompat)
    implementation(AndroidX.dataStore.preferences)

    implementation(AndroidX.activity.compose)
    implementation(AndroidX.lifecycle.viewModelCompose)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.runtime.liveData)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.ui)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.fragment.ktx)
    implementation(AndroidX.preference.ktx)
    implementation(AndroidX.lifecycle.runtimeKtx)

    implementation(Google.dagger.hilt.android)
    implementation(AndroidX.hilt.navigationCompose)
    kapt(Google.dagger.hilt.compiler)

    implementation(Google.android.material)
    implementation(KotlinX.serialization.json)

    implementation("com.github.topjohnwu.libsu:core:_")
    implementation("com.github.topjohnwu.libsu:service:_")

    implementation("org.tukaani:xz:_")
    implementation("org.apache.commons:commons-compress:_")

    implementation("com.mikepenz:aboutlibraries-core:_")

    implementation("dev.rikka.shizuku:api:_")
    implementation("dev.rikka.shizuku:provider:_")

    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:_")

    compileOnly(project(":hidden-api-stub"))
}