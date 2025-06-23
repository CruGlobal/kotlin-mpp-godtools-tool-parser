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
        commonMain {
            dependencies {
                api(project(":module:parser"))
                api(project(":module:renderer-state"))

                implementation(compose.runtime)
                implementation(compose.material3)

                implementation(libs.coil.compose)
                implementation(libs.colormath.jetpack.compose)
                implementation(libs.compottie)
                implementation(libs.compottie.dot)
                implementation(libs.gtoSupport.compose)
                implementation(libs.gtoSupport.okio)
            }
        }
        commonTest {
            dependencies {
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)

                implementation(libs.coil.test)
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
