plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.common"
}

kotlin {
    configureJsTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.colormath)
                implementation(libs.napier)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.colormath.jetpack.compose)
            }
        }
    }
}
