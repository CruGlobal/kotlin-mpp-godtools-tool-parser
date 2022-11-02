import org.ajoberstar.grgit.Grgit

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    alias(libs.plugins.grgit)
    alias(libs.plugins.kotlin.kover)
    alias(libs.plugins.ktlint)
}

allprojects {
    group = "org.cru.godtools.kotlin"

    // configure the project version
    if (!project.findProperty("releaseBuild")?.toString().toBoolean()) {
        project.findProperty("versionSuffix")?.toString()
            ?.takeIf { it.matches(Regex("\\S+")) }
            ?.let { version = "$version-$it" }
        version = "$version-SNAPSHOT"
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
                        implementation(kotlin("test"))
                        implementation(libs.kotlin.coroutines.test)
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
    configureIosTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:analytics"))
                api(project(":module:parser"))
                api(project(":module:state"))
                api(project(":module:user-activity"))
            }
        }
    }

    cocoapods {
        summary = "GodTools tool parser"
        license = "MIT"
        homepage = "https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser"

        framework {
            baseName = "GodToolsToolParser"

            export(project(":module:analytics"))
            export(project(":module:parser"))
            export(project(":module:state"))
            export(project(":module:user-activity"))
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

// region Kotlin Kover
koverMerged {
    enable()

    filters {
        classes {
            excludes += listOf(
                // exclude SaxXmlPullParser from reports because it is only used by iOS and JS
                // TODO: remove this if we ever support coverage reports for iOS or js
                "**.SaxXmlPullParser*",
                // exclude the generated ANTLR StateExpression grammar parser
                "org.cru.godtools.expressions.grammar.generated.*",
                // test classes/sourceSets are not automatically filtered currently
                "org.cru.godtools.**.*Test",
                "org.cru.godtools.**.*Test$*",
                "org.cru.godtools.**.*TestKt",
            )
        }
    }
}
// endregion Kotlin Kover

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
