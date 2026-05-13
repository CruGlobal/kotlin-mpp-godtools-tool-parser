import dev.petuska.npm.publish.task.NpmPublishTask

plugins {
    id("build-logic")
    kotlin("multiplatform")
    alias(libs.plugins.grgit)
    alias(libs.plugins.npm.publish)
}

kotlin {
    configureJsTargets()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":module:parser"))
                implementation(project(":module:renderer-state"))
            }
        }
    }
}
configureKtlint()

npmPublish {
    organization.set("cruglobal")

    if (project.isSnapshotVersion) {
        version.set(grgitService.service.map { "${project.version}.${it.grgit.log().size}" })
    }
    packages {
        named("js") {
            packageName.set("godtools-shared")
            packageJson {
                repository {
                    url.set("https://github.com/CruGlobal/kotlin-mpp-godtools-tool-parser")
                }
            }
        }
    }
    registries {
        npmjs {}
    }
}
tasks.withType<NpmPublishTask> {
    val distTag = findProperty("npmPublishTag")?.toString()
    when {
        !distTag.isNullOrBlank() -> tag.set(distTag)
        isSnapshotVersion -> tag.set("snapshot")
    }
}
