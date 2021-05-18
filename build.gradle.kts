import org.ajoberstar.grgit.Grgit

plugins {
    kotlin("multiplatform") version "1.5.0"
    kotlin("native.cocoapods") version "1.5.0"
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.5.0"
    id("org.ajoberstar.grgit") version "4.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("com.vanniktech.android.junit.jacoco") version "0.16.0"
    jacoco
}

group = "org.cru.godtools.kotlin"
version = "0.1.0-SNAPSHOT"

val isSnapshotVersion get() = version.toString().endsWith("-SNAPSHOT")

repositories {
    google()
    mavenCentral()
}
kotlin {
    android {
        publishLibraryVariants("debug", "release")
    }
    // HACK: workaround https://youtrack.jetbrains.com/issue/KT-40975
    //       See also: https://kotlinlang.org/docs/mobile/add-dependencies.html#workaround-to-enable-ide-support-for-the-shared-ios-source-set
    //       This should be able to go away when we upgrade to Kotlin 1.5.20
//    ios()
    when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> iosArm64("ios")
        else -> iosX64("ios")
    }
    js {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("androidx.test.ext:junit:1.1.2")
                implementation("org.robolectric:robolectric:4.5.1")
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
        val test by getting { setRoot("src/androidTest") }
        val androidTest by getting { setRoot("src/androidAndroidTest") }
    }
}

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

jacoco {
    toolVersion = "0.8.7"
}
