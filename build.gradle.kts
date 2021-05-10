import org.jetbrains.kotlin.gradle.tasks.PodspecTask

plugins {
    kotlin("multiplatform") version "1.5.0"
    kotlin("native.cocoapods") version "1.5.0"
    id("com.android.library")
    id("maven-publish")
    id("org.ajoberstar.grgit") version "4.1.0"
}

group = "org.cru.godtools.kotlin"
version = "0.1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}
kotlin {
    android {
        publishLibraryVariants("release")
    }
    iosArm32()
    iosArm64()
    iosX64()

    cocoapods {
        summary = "GodTools tool parser"
        license = "MIT"
        homepage = "https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser"

        frameworkName = "GodToolsToolParser"

        ios.deploymentTarget = "11.0"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting
        val androidTest by getting
//        val iosMain by getting
//        val iosTest by getting
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
    val podspec = file("${project.name.replace("-", "_")}.podspec")
    val newPodspecContent = podspec.readLines().map {
        when {
            it.contains("spec.source") -> """
                |#$it
                |    spec.source                   = {
                |                                      :git => "https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser.git",
                |                                      ${
                    when {
                        project.version.toString().endsWith("-SNAPSHOT") -> ":commit => \"${grgit.head().id}\""
                        else -> ":tag => \"v${project.version}\""
                    }
                }
                |                                    }
            """.trimMargin()
            it.contains("vendored_frameworks") -> """
                |$it
                |    spec.prepare_command          = "mkdir -p ${it.substringAfter('"').substringBeforeLast('"')}"
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
    delete("GodToolsToolParser.podspec")
}.also { tasks["clean"].dependsOn(it) }
