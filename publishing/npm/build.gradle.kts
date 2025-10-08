import dev.petuska.npm.publish.task.NpmPublishTask

plugins {
    id("build-logic")
    kotlin("multiplatform")
    id("org.ajoberstar.grgit.service")
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

npmPublish {
    organization.set("cruglobal")

    if (project.isSnapshotVersion) {
        version.set(grgitService.service.map { "${project.version}.${it.grgit.log().size}" })
    }
    packages {
        named("js") {
            packageName.set("godtools-shared")
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
