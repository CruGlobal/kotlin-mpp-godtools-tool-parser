import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

internal fun KotlinMultiplatformExtension.configureCommonSourceSets() {
    sourceSets.named("commonTest") {
        dependencies {
            implementation(kotlin("test"))
            implementation(project.libs.findBundle("common-test-framework").get())
        }
    }
}

internal fun KotlinMultiplatformExtension.configureAndroidTargets() {
    androidTarget {
        publishLibraryVariants("debug", "release")
    }

    sourceSets.named("androidUnitTest") {
        dependencies {
            implementation(project.libs.findBundle("android-test-framework").get())
        }
    }
}

fun KotlinMultiplatformExtension.configureIosTargets() {
    iosX64 { copyTestResources() }
    iosArm64 { copyTestResources() }
    iosSimulatorArm64 { copyTestResources() }
}

fun KotlinMultiplatformExtension.configureJsTargets() {
    js {
        binaries.library()
        browser {
            testTask { useMocha() }
        }
        generateTypeScriptDefinitions()
    }
}

// region iOS Test Resources
// HACK: workaround https://youtrack.jetbrains.com/issue/KT-37818
//       based on logic found here: https://github.com/icerockdev/moko-resources/pull/107/files
private fun KotlinNativeTarget.copyTestResources() {
    binaries
        .matching { it is TestExecutable }
        .configureEach {
            linkTaskProvider.configure {
                doLast {
                    project.file("src/commonTest/resources")
                        .takeIf { it.exists() && it.isDirectory }
                        ?.copyRecursively(
                            target = outputDirectory,
                            overwrite = true
                        )
                }
            }
        }
}
// endregion iOS Test Resources
