plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.common"
    baseConfiguration()
}
enablePublishing()

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.colormath)
                implementation(libs.napier)
            }
        }
    }
}
