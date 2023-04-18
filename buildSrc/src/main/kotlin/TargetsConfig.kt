import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
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
    android {
        publishLibraryVariants("debug", "release")
    }

    sourceSets.named("androidUnitTest") {
        dependencies {
            implementation(project.libs.findBundle("android-test-framework").get())
        }
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
