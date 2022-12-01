plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.analytics"
    baseConfiguration()
}
enablePublishing()

kotlin {
    configureAndroidTargets()
    configureIosTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.gtoSupport.androidx.annotation)
            }
        }
    }
}
