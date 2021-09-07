import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("org.ajoberstar.grgit") version "4.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("com.vanniktech.android.junit.jacoco") version "0.16.0"
}

allprojects {
    group = "org.cru.godtools.kotlin"
    version = "0.3.0-SNAPSHOT"

    repositories {
        maven("https://jitpack.io") {
            content {
                includeGroup("com.strumenta.antlr-kotlin")
            }
        }
        maven("https://cruglobal.jfrog.io/artifactory/maven-mobile/") {
            content {
                includeGroup("org.cru.mobile.fork.antlr-kotlin")
            }
        }
        google()
        mavenCentral()
    }
}

subprojects {
    afterEvaluate {
        // don't evaluate if kotlin isn't enabled for this project
        if (extensions.findByName("kotlin") == null) return@afterEvaluate

        kotlin {
            sourceSets {
                val commonTest by getting {
                    dependencies {
                        implementation(project(":test-fixtures"))

                        implementation(kotlin("test"))
                    }
                }
                val androidTest by getting {
                    dependencies {
                        implementation(libs.androidx.test.junit)
                        implementation(libs.robolectric)
                    }
                }
            }
        }
    }
}

kotlin {
    configureIosTargets {
        binaries {
            withType(Framework::class.java).configureEach {
                export(project(":module:parser"))
                export(project(":module:state"))
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:parser"))
                api(project(":module:state"))
            }
        }
    }

    cocoapods {
        summary = "GodTools tool parser"
        license = "MIT"
        homepage = "https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser"

        framework {
            baseName = "GodToolsToolParser"
        }

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
                    |    SANITIZED_CONFIGURATION=Debug
                    |else
                    |    SANITIZED_CONFIGURATION=Release
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
        "**/SaxXmlPullParser*",

        // exclude the generated ANTLR StateExpression grammar parser
        "**/grammar/generated/StateExpression*"
    )
}
subprojects {
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
allprojects {
    beforeEvaluate {
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        ktlint {
            version.set(libs.versions.ktlint)
        }
    }
}
// endregion KtLint
