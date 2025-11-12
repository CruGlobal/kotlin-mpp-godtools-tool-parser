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

    sourceSets {
        getByName("test") {
            resources.srcDir("src/commonTest/resources")
        }
    }
}

compose.resources {
    packageOfResClass = "org.cru.godtools.shared.renderer.generated.resources"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":module:parser"))
                api(project(":module:renderer-state"))

                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)
                implementation(compose.runtime)

                api(libs.circuit.runtime)
                implementation(libs.androidx.graphics.shapes)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.coil.compose)
                implementation(libs.colormath.jetpack.compose)
                implementation(libs.compose.media.player)
                implementation(libs.compottie)
                implementation(libs.compottie.dot)
                implementation(libs.gtoSupport.androidx.lifecycle)
                implementation(libs.gtoSupport.compose)
                implementation(libs.gtoSupport.okio)
            }
        }
        commonTest {
            dependencies {
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.uiTest)

                implementation(libs.androidx.lifecycle.testing)
                implementation(libs.circuit.test)
                implementation(libs.coil.test)
                implementation(libs.gtoSupport.androidx.test.junit)
                implementation(libs.turbine)
            }
        }
        androidUnitTest {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)

                implementation(libs.testparameterinjector)
            }
        }
    }
}
