import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl

fun KotlinMultiplatformExtension.configureTargets() {
    configureAndroidTargets()
    configureIosTargets()
    configureJsTargets()
}

fun KotlinMultiplatformExtension.configureAndroidTargets() {
    android {
        publishLibraryVariants("debug", "release")
    }
}

fun KotlinMultiplatformExtension.configureIosTargets() {
    ios { copyTestResources() }

    // enable running ios tests on a background thread as well
    // configuration copied from: https://github.com/square/okio/pull/929
    targets.withType(KotlinNativeTargetWithTests::class.java) {
        binaries {
            // Configure a separate test where code runs in background
            test("background", setOf(DEBUG)) {
                freeCompilerArgs += "-trw"
            }
        }
        testRuns.create("background") {
            setExecutionSourceFrom(binaries.getByName("backgroundDebugTest") as TestExecutable)
        }
    }
}

fun KotlinMultiplatformExtension.configureJsTargets() {
    js {
        nodejs { copyTestResources() }
    }
}

// region iOS Test Resources
// HACK: workaround https://youtrack.jetbrains.com/issue/KT-37818
//       based on logic found here: https://github.com/icerockdev/moko-resources/pull/107/files
private fun KotlinNativeTarget.copyTestResources() {
    binaries
        .matching { it is TestExecutable }
        .configureEach {
            (this as TestExecutable).linkTask.doLast {
                project.file("src/commonTest/resources")
                    .takeIf { it.exists() && it.isDirectory }
                    ?.copyRecursively(
                        target = outputDirectory,
                        overwrite = true
                    )
            }
        }
}
// endregion iOS Test Resources

// region Js Test Resources
private fun KotlinJsSubTargetDsl.copyTestResources() {
    testTask {
        // TODO: copy resources out of processedResources instead.
        val source = project.file("src/commonTest/resources").takeIf { it.exists() && it.isDirectory }
            ?: return@testTask

        val compileTask = compilation.compileKotlinTaskProvider.get()
        val target = compileTask.outputFileProperty.get().resolve("../../resources").normalize()
        compileTask.doLast { source.copyRecursively(target = target, overwrite = true) }

        // add target resources directory to appropriate task inputs/outputs
        compileTask.inputs.dir(source)
        compileTask.outputs.dir(target)
        inputs.dir(target)
    }
}
// endregion Js Test Resources
