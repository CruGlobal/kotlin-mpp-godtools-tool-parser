import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

fun KotlinMultiplatformExtension.configureIosTargets() {
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
