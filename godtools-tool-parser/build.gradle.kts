import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

val isSnapshotVersion get() = version.toString().endsWith("-SNAPSHOT")

kotlin {
    android {
        publishLibraryVariants("debug", "release")
    }
    // HACK: workaround https://youtrack.jetbrains.com/issue/KT-40975
    //       See also: https://kotlinlang.org/docs/mobile/add-dependencies.html#workaround-to-enable-ide-support-for-the-shared-ios-source-set
    //       This should be able to go away when we upgrade to Kotlin 1.5.30
//    ios { copyTestResources() }
    when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> iosArm64("ios")
        else -> iosX64("ios")
    }.copyTestResources()
    js {
        nodejs { copyTestResources() }
    }

    // enable running ios tests on a background thread as well
    // configuration copied from: https://github.com/square/okio/pull/929
    targets.withType<KotlinNativeTargetWithTests<*>> {
        binaries {
            // Configure a separate test where code runs in background
            test("background", setOf(DEBUG)) {
                freeCompilerArgs += "-trw"
            }
        }
        testRuns {
            val background by creating {
                setExecutionSourceFrom(binaries.getByName("backgroundDebugTest") as TestExecutable)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

                implementation(libs.colormath)
                implementation(libs.fluidLocale)
                implementation(libs.napier)
                implementation(libs.splitties.bitflags)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("androidx.test.ext:junit:1.1.3")
                implementation("org.robolectric:robolectric:4.6.1")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("sax", "1.2.4"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(libs.okio.js)
                implementation(libs.okio.nodefilesystem)
            }
        }
    }
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    sourceSets {
        val main by getting { setRoot("src/androidMain") }
        val test by getting {
            setRoot("src/androidTest")
            resources.srcDir("src/commonTest/resources")
        }
        val androidTest by getting { setRoot("src/androidAndroidTest") }
    }
}

// region iOS Test Resources
// HACK: workaround https://youtrack.jetbrains.com/issue/KT-37818
//       based on logic found here: https://github.com/icerockdev/moko-resources/pull/107/files
fun KotlinNativeTarget.copyTestResources() {
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
fun KotlinJsSubTargetDsl.copyTestResources() {
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
