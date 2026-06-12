plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.paparazzi)
}

compose.resources {
    packageOfResClass = "org.cru.godtools.shared.renderer.generated.resources"
}

kotlin {
    androidLibrary {
        namespace = "org.cru.godtools.shared.renderer"

        androidResources.enable = true

        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":module:parser"))
                api(project(":module:renderer-state"))

                implementation(libs.compose.components.resources)
                implementation(libs.compose.material.icons.extended)
                implementation(libs.compose.material3)
                implementation(libs.compose.runtime)

                api(libs.circuit.runtime)
                api(libs.kotlin.immutable.collections)
                implementation(libs.androidx.graphics.shapes)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.coil.compose)
                implementation(libs.colormath.jetpack.compose)
                implementation(libs.compose.media.player)
                implementation(libs.compottie)
                implementation(libs.compottie.dot)
                implementation(libs.gtoSupport.compose)
                implementation(libs.gtoSupport.okio)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.compose.ui.test)

                implementation(libs.androidx.lifecycle.testing)
                implementation(libs.circuit.test)
                implementation(libs.coil.test)
                implementation(libs.gtoSupport.androidx.test.junit)
                implementation(libs.turbine)
            }
        }
        getByName("androidHostTest") {
            dependencies {
                implementation(libs.androidx.compose.ui.test.manifest)

                implementation(libs.testparameterinjector)
            }
        }
    }
}
