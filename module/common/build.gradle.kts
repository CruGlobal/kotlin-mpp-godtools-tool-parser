plugins {
    id("godtools-shared.module-conventions")
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
                implementation(libs.kermit)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.colormath.jetpack.compose)
            }
        }
    }
}
