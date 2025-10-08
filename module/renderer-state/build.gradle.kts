import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("godtools-shared.module-conventions")
    kotlin("plugin.parcelize")
}

android {
    namespace = "org.cru.godtools.shared.renderer.state"
}

kotlin {
    configureJsTargets()

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions.freeCompilerArgs.addAll(
            "-P",
            "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=" +
                "org.ccci.gto.android.common.parcelize.Parcelize",
        )
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":module:parser-expressions"))
                implementation(project(":module:parser"))

                implementation(libs.androidx.annotation)
                implementation(libs.gtoSupport.parcelize)
                implementation(libs.kotlin.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.turbine)
            }
        }
    }
}
