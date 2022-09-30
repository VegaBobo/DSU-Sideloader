buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath(Google.dagger.hilt.android.gradlePlugin)
        classpath("org.jetbrains.kotlin:kotlin-serialization:_")
    }
}// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") apply false
    id("com.android.library") apply false

    id("org.jetbrains.kotlin.android") apply false
    id("org.jetbrains.kotlin.multiplatform") apply false
    id("org.jetbrains.kotlin.plugin.serialization") apply false

    id("com.mikepenz.aboutlibraries.plugin") apply false
}

val versionCode by extra { 4 }
val versionName by extra { "1.03" }
val packageName by extra { "vegabobo.dsusideloader" }

task("clean") {
    delete(project.buildDir)
}