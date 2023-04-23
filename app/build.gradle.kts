import java.util.Properties

fun getReleaseSigningConfig(): File {
    return File(".sign/dsu_sideloader.prop")
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.mikepenz.aboutlibraries.plugin")
    id("kotlinx-serialization")
    id("org.jmailen.kotlinter")
}

android {
    val versionCode: Int by rootProject.extra
    val versionName: String by rootProject.extra
    val packageName: String by rootProject.extra

    namespace = packageName
    compileSdk = 33

    defaultConfig {
        this.applicationId = packageName
        this.versionCode = versionCode
        this.versionName = versionName

        minSdk = 29
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        val releaseSigningConfig = getReleaseSigningConfig()
        if (releaseSigningConfig.exists()) {
            create("release") {
                /**
                 * .sign/dsu_sideloader.prop
                 *
                 * keystore=some/path/keystore.jks
                 * keystore_pw=keystore_password
                 * alias=alias
                 * alias_pw=alias_password
                 *
                 */
                val props = Properties()
                props.load(releaseSigningConfig.inputStream())

                storeFile = File(props.getProperty("keystore"))
                storePassword = props.getProperty("keystore_pw")
                keyAlias = props.getProperty("alias")
                keyPassword = props.getProperty("alias_pw")
            }
        }
    }

    buildTypes {
        getByName("release") {
            if (getReleaseSigningConfig().exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        create("miniDebug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
            isMinifyEnabled = true
            isShrinkResources = true
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
    kotlin {
        jvmToolchain(17)
    }
    buildFeatures {
        aidl = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

aboutLibraries {
    // Remove the "generated" timestamp to allow for reproducible builds
    excludeFields = arrayOf("generated")
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
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.runtime.liveData)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.ui.toolingPreview)
    implementation(AndroidX.compose.ui)

    implementation(AndroidX.core.ktx)
    implementation(AndroidX.fragment.ktx)
    implementation(AndroidX.preference.ktx)
    implementation(AndroidX.lifecycle.runtime.ktx)

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

tasks {
    "preBuild" {
        dependsOn(lintKotlin)
    }
    "lintKotlin" {
        dependsOn(formatKotlin)
    }
}
