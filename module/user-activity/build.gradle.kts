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
    configureCommonSourceSets()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.materialColorUtilities)
            }
        }
        val commonMain by getting {
            dependencies {
                api(project(":module:common"))

                implementation(libs.colormath)
                implementation(libs.fluidLocale)
                implementation(libs.gtoSupport.androidx.annotation)
                implementation(libs.gtoSupport.fluidsonic.locale)
                implementation(libs.okio)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.gtoSupport.androidx.test.junit)
            }
        }
    }
}
