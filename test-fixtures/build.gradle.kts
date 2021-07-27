plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

configureAndroidLibrary()

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlin.coroutines.core)
            }
        }
    }
}
