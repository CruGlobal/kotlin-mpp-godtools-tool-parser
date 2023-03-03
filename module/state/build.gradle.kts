plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.parcelize")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.tool.state"
    baseConfiguration()
}
enablePublishing()

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.gtoSupport.androidx.annotation)
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
