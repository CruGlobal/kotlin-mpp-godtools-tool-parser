import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl

fun KotlinMultiplatformExtension.configureIosTargets(configure: KotlinNativeTarget.() -> Unit = {}) {
    // HACK: workaround https://youtrack.jetbrains.com/issue/KT-40975
    //       See also: https://kotlinlang.org/docs/mobile/add-dependencies.html#workaround-to-enable-ide-support-for-the-shared-ios-source-set
    //       This should be able to go away when we upgrade to Kotlin 1.5.30
//    ios { copyTestResources() }
    val target = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> iosArm64("ios")
        else -> iosX64("ios")
    }
    target.copyTestResources()
    target.configure()

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
                project.file("src/commonTest/resources").copyRecursively(
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
        val compileTask = compilation.compileKotlinTaskProvider.get()
        compileTask.doLast {
            // TODO: copy resources out of processedResources instead.
            project.file("src/commonTest/resources").copyRecursively(
                target = compileTask.outputFileProperty.get().resolve("../../resources").normalize(),
                overwrite = true
            )
        }
    }
}
// endregion Js Test Resources
