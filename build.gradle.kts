import org.ajoberstar.grgit.Grgit
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

plugins {
    id("build-logic")
    kotlin("multiplatform") version libs.versions.kotlin
    kotlin("native.cocoapods") version libs.versions.kotlin
    alias(libs.plugins.grgit)
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
        commonMain {
            dependencies {
                api(project(":module:analytics"))
                api(project(":module:interop"))
                api(project(":module:parser"))
                api(project(":module:parser-base"))
                api(project(":module:renderer"))
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

        val frameworkBaseName = "GodToolsToolParser"
        framework {
            baseName = frameworkBaseName

            export(project(":module:analytics"))
            export(project(":module:interop"))
            export(project(":module:parser"))
            export(project(":module:parser-base"))
            export(project(":module:renderer"))
            export(project(":module:user-activity"))
        }

        // HACK: workaround a generated framework existence check added in Kotlin 1.9.20
        //       We automatically generate the dummy framework via the spec.prepare_command
        val framework = layout.buildDirectory
            .dir("cocoapods/framework/$frameworkBaseName.framework").get().asFile
            .relativeTo(projectDir)
        extraSpecAttributes += "vendored_frameworks" to """"${framework.invariantSeparatorsPath}""""

        ios.deploymentTarget = "14.0"
    }
}
configureKtlint()

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
