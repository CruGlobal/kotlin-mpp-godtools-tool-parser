import dev.petuska.npm.publish.task.NpmPublishTask

plugins {
    kotlin("multiplatform")
    id("org.ajoberstar.grgit.service")
    alias(libs.plugins.npm.publish)
}

kotlin {
    configureJsTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":module:parser"))
                implementation(project(":module:state"))
            }
        }
        val jsMain by getting {
            dependencies {
                // HACK: we currently need to directly include all transitive npm dependencies
                implementation(npmLibs.sax)
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
        val token = findProperty("npmPublishRegistryNpmjsAuthToken")
        if (token != null) {
            npmjs {
                authToken.set(token.toString())
            }
        }
    }
}
tasks.withType<NpmPublishTask> {
    val distTag = findProperty("npmPublishTag")?.toString()
    when {
        !distTag.isNullOrBlank() -> tag.set(distTag)
        isSnapshotVersion -> tag.set("snapshot")
    }
}

// HACK: workaround https://github.com/mpetuska/npm-publish/issues/110
tasks.withType<NpmPublishTask> {
    dependsOn(rootProject.tasks.named("kotlinNodeJsSetup"))
}
