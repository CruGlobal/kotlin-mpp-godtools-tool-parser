import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "org.cru.godtools.shared.renderer"

    testOptions.unitTests.isIncludeAndroidResources = true
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:parser"))

                implementation(compose.runtime)
                implementation(compose.material3)

                implementation(libs.colormath.jetpack.compose)
            }
        }
        commonTest {
            dependencies {
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)

                implementation(libs.gtoSupport.androidx.test.junit)
                implementation(libs.turbine)
            }
        }
        androidUnitTest {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)
            }
        }
    }
}
