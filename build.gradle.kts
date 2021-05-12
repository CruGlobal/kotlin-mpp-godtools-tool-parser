import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.tasks.PodspecTask

plugins {
    kotlin("multiplatform") version "1.5.0"
    kotlin("native.cocoapods") version "1.5.0"
    id("com.android.library")
    id("maven-publish")
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
        publishLibraryVariants("release")
    }
    ios()
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
        val androidMain by getting
        val androidTest by getting
        val iosMain by getting
        val iosTest by getting
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
}

// HACK: customize the podspec until KT-42105 is implemented
//       https://youtrack.jetbrains.com/issue/KT-42105
(tasks["podspec"] as PodspecTask).doLast {
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
tasks.create("cleanPodspec", Delete::class) {
    delete("${project.name.replace('-', '_')}.podspec")
}.also { tasks["clean"].dependsOn(it) }
// endregion Cocoapods

jacoco {
    toolVersion = "0.8.7"
}
