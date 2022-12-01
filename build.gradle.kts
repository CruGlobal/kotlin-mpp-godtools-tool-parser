import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

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
        name = "GodToolsShared"
        summary = "GodTools shared logic"
        license = "MIT"
        homepage = "https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser"
        extraSpecAttributes += mapOf(
            "prepare_command" to """"./gradlew generateDummyFramework"""",
            "preserve_paths" to """"**/*.*"""",
        )

        // configure the custom xcode configurations in the godtools-swift project
        xcodeConfigurationToNativeBuildType += mapOf(
            "AnalyticsLogging" to NativeBuildType.DEBUG,
            "Staging" to NativeBuildType.DEBUG,
            "Production" to NativeBuildType.DEBUG,
        )

        framework {
            baseName = "GodToolsToolParser"
            isStatic = true

            export(project(":module:analytics"))
            export(project(":module:parser"))
            export(project(":module:state"))
            export(project(":module:user-activity"))
        }

        ios.deploymentTarget = "14.0"
    }
}

// region Cocoapods
// HACK: customize the podspec until KT-42105 is implemented
//       https://youtrack.jetbrains.com/issue/KT-42105
tasks.podspec.configure {
    doLast {
        // we can't use the grgit extension val because it won't be present if the .git directory is missing
        val grgit = project.extensions.findByName("grgit") as? Grgit
        val newPodspecContent = outputFile.readLines().map {
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

                else -> it
            }
        }
        outputFile.writeText(newPodspecContent.joinToString(separator = "\n"))
    }
}
tasks.create("cleanPodspec", Delete::class) {
    delete(tasks.podspec.map { it.outputFile })
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
