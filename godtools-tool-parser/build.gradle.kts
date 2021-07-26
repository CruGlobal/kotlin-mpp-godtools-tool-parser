import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsSubTargetDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

kotlin {
    android {
        publishLibraryVariants("debug", "release")
    }
    configureIosTargets()
    js {
        nodejs { copyTestResources() }
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
