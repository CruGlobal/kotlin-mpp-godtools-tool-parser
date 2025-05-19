import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("godtools-shared.module-conventions")
    kotlin("plugin.parcelize")
}

android {
    namespace = "org.cru.godtools.shared.tool.parser.base"
}

kotlin {
    configureJsTargets()

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions.freeCompilerArgs.addAll(
            "-P",
            "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=" +
                "org.cru.godtools.shared.tool.state.internal.Parcelize",
        )
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.kotlin.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.turbine)
            }
        }
    }
}
