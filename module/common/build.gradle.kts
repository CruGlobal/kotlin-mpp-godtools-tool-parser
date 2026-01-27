plugins {
    id("godtools-shared.module-conventions")
}

android {
    namespace = "org.cru.godtools.shared.common"
}

kotlin {
    configureJsTargets()

    sourceSets {
        commonMain {
            dependencies {
                api(libs.colormath)
                implementation(libs.kermit)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.gtoSupport.androidx.test.junit)
            }
        }
        androidMain {
            dependencies {
                api(libs.colormath.jetpack.compose)
            }
        }
    }
}
