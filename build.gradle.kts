import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

plugins {
    kotlin("multiplatform") version "1.5.20"
    kotlin("native.cocoapods") version "1.5.20"
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.5.20"
    id("org.ajoberstar.grgit") version "4.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("com.vanniktech.android.junit.jacoco") version "0.16.0"
    jacoco
}

group = "org.cru.godtools.kotlin"
version = "0.1.0"

val isSnapshotVersion get() = version.toString().endsWith("-SNAPSHOT")

repositories {
    google()
    mavenCentral()
    jcenter {
        content {
            includeGroup("com.louiscad.splitties")
        }
    }
}
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
        nodejs()
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
                implementation(libs.fluidLocale)
                implementation(libs.napier)
                implementation(libs.splitties.bitflags)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
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
                implementation("androidx.test.ext:junit:1.1.2")
                implementation("org.robolectric:robolectric:4.5.1")
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

    cocoapods {
        summary = "GodTools tool parser"
        license = "MIT"
        homepage = "https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser"

        frameworkName = "GodToolsToolParser"

        ios.deploymentTarget = "11.0"
    }
    publishing {
        repositories {
            maven {
                name = "cruGlobalMavenRepository"
                setUrl(
                    when {
                        isSnapshotVersion ->
                            "https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-snapshots-local/"
                        else -> "https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-releases-local/"
                    }
                )

                credentials(PasswordCredentials::class)
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

// region Jacoco
junitJacoco {
    jacocoVersion = libs.versions.jacoco.get()
    includeNoLocationClasses = true
    excludes = listOf(
        // we exclude SaxXmlPullParser from reports because it is only used by iOS and JS
        "**/SaxXmlPullParser*"
    )
}
tasks.withType(Test::class.java) {
    extensions.configure(JacocoTaskExtension::class.java) {
        excludes = excludes.orEmpty() + "jdk.internal.*"
    }
}
tasks.create("jacocoTestReport") {
    dependsOn(tasks.withType(JacocoReport::class.java))
}
// endregion Jacoco

// region KtLint
ktlint {
    version.set(libs.versions.ktlint)
}
// endregion KtLint

// region Cocoapods
// HACK: customize the podspec until KT-42105 is implemented
//       https://youtrack.jetbrains.com/issue/KT-42105
tasks.podspec.configure {
    doLast {
        // we can't use the grgit extension val because it won't be present if the .git directory is missing
        val grgit = project.extensions.findByName("grgit") as? Grgit
        val podspec = file("${project.name.replace("-", "_")}.podspec")
        val newPodspecContent = podspec.readLines().map {
            when {
                grgit != null && it.contains("spec.source") -> {
                    val ref = when {
                        isSnapshotVersion -> ":commit => \"${grgit.head().id}\""
                        else -> ":tag => \"v${project.version}\""
                    }
                    """
                        |#$it
                        |    spec.source                   = {
                        |                                      :git => "https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser.git",
                        |                                      $ref
                        |                                    }
                    """.trimMargin()
                }
                it.contains("vendored_frameworks") -> """
                    |$it
                    |    spec.prepare_command          = "./gradlew generateDummyFramework"
                """.trimMargin()
                it == "end" -> """
                    |    spec.preserve_paths           = "**/*.*"
                    |$it
                """.trimMargin()

                // HACK: force CONFIGURATION to be debug or release only.
                //       other values are not currently supported by the kotlin cocoapods plugin
                it.contains("syncFramework") -> """
                    |if [[ ${'$'}(echo ${'$'}CONFIGURATION | tr '[:upper:]' '[:lower:]') = 'debug' ]]
                    |then
                    |    SANITIZED_CONFIGURATION=debug
                    |else
                    |    SANITIZED_CONFIGURATION=release
                    |fi
                    |$it
                """.trimMargin()
                it.contains("\$CONFIGURATION") -> it.replace("CONFIGURATION", "SANITIZED_CONFIGURATION")

                else -> it
            }
        }
        podspec.writeText(newPodspecContent.joinToString(separator = "\n"))
    }
}
tasks.create("cleanPodspec", Delete::class) {
    delete("${project.name.replace('-', '_')}.podspec")
}.also { tasks.clean.configure { dependsOn(it) } }
// endregion Cocoapods

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
