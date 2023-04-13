import org.ajoberstar.grgit.Grgit

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.grgit)
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

    val grgit = project.extensions.findByName("grgit") as? Grgit
    if (grgit != null && project.isSnapshotVersion) version.set("${project.version}.${grgit.log().size}")
    packages {
        named("js") {
            packageName.set("godtools-shared")
        }
    }
    registries {
        npmjs {
            authToken.set(findProperty("npmPublishRegistryNpmjsAuthToken")?.toString())
        }
    }
}
