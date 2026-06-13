import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    id("godtools-shared.module-conventions")
    kotlin("plugin.parcelize")
}

kotlin {
    android {
        namespace = "org.cru.godtools.shared.renderer.state"

        withHostTest { }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions.freeCompilerArgs.addAll(
            "-P",
            "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=" +
                "org.ccci.gto.android.common.parcelize.Parcelize",
        )
    }
    configureJsTargets()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":module:common"))
                api(project(":module:parser-expressions"))
                implementation(project(":module:parser"))

                api(libs.gtoSupport.parcelize)
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
