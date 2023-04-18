plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.analytics"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.gtoSupport.androidx.annotation)
            }
        }
    }
}
