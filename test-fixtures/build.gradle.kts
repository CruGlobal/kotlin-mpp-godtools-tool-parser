plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    configureSdk()
    configureSourceSets()
}

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
