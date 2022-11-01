plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.user.activity"
    baseConfiguration()
}
enablePublishing()

kotlin {
    configureAndroidTargets()
    configureIosTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.fluidLocale)
                implementation(libs.gtoSupport.fluidsonic.locale)
            }
        }
    }
}
