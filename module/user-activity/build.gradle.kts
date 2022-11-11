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
                implementation(project(":module:common"))

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
