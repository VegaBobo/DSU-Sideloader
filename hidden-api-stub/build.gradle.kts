plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "vegabobo.dsusideloader"
    compileSdk = 33

    defaultConfig {
        minSdk = 29
    }
    buildTypes {
        create("miniDebug"){}
    }
}

dependencies {
    implementation("dev.rikka.shizuku:api:_")
    implementation("dev.rikka.shizuku:provider:_")
}