import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework

plugins {
    kotlin("multiplatform") version "1.5.20"
    kotlin("native.cocoapods") version "1.5.20"
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.5.20" apply false
    id("org.ajoberstar.grgit") version "4.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("com.vanniktech.android.junit.jacoco") version "0.16.0"
}

val isSnapshotVersion get() = version.toString().endsWith("-SNAPSHOT")

allprojects {
    group = "org.cru.godtools.kotlin"
    version = "0.2.0-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
        jcenter {
            content {
                includeGroup("com.louiscad.splitties")
            }
        }
    }
}

kotlin {
    // HACK: workaround https://youtrack.jetbrains.com/issue/KT-40975
    //       See also: https://kotlinlang.org/docs/mobile/add-dependencies.html#workaround-to-enable-ide-support-for-the-shared-ios-source-set
    //       This should be able to go away when we upgrade to Kotlin 1.5.30
//    ios()
    when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> iosArm64("ios")
        else -> iosX64("ios")
    }.apply {
        binaries {
            withType(Framework::class.java).configureEach {
                export(project(":godtools-tool-parser"))
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":godtools-tool-parser"))
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

// region Jacoco
junitJacoco {
    jacocoVersion = libs.versions.jacoco.get()
    includeNoLocationClasses = true
    excludes = listOf(
        // we exclude SaxXmlPullParser from reports because it is only used by iOS and JS
        "**/SaxXmlPullParser*"
    )
}
allprojects {
    apply(plugin = "org.gradle.jacoco")
    tasks.withType(Test::class.java) {
        extensions.configure(JacocoTaskExtension::class.java) {
            excludes = excludes.orEmpty() + "jdk.internal.*"
        }
    }
    tasks.create("jacocoTestReport") {
        dependsOn(tasks.withType(JacocoReport::class.java))
    }
}
// endregion Jacoco

// region KtLint
ktlint {
    version.set(libs.versions.ktlint)
}
// endregion KtLint
