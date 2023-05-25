plugins {
    id("godtools-shared.module-conventions")
    kotlin("plugin.parcelize")
}

android {
    namespace = "org.cru.godtools.shared.tool.state"
}

kotlin {
    configureJsTargets()

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
