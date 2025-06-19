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
                "org.cru.godtools.shared.renderer.state.internal.Parcelize",
        )
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":module:common"))
                api(project(":module:parser-expressions"))
                implementation(project(":module:parser-base"))

                implementation(libs.androidx.annotation)
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
