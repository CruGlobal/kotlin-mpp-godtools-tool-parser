plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.parcelize")
    alias(libs.plugins.kotlin.kover)
}

configureAndroidLibrary()
enablePublishing()

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.turbine)
            }
        }
    }
}
