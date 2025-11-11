pluginManagement {
    repositories {
        google()
        maven("https://cruglobal.jfrog.io/artifactory/maven-mobile/") {
            content {
                includeGroup("org.cru.mobile.fork.antlr-kotlin")
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io") {
            content {
                includeGroup("com.strumenta.antlr-kotlin")
                includeGroupByRegex("com\\.github\\..*")
            }
        }
        maven("https://raw.githubusercontent.com/Deezer/KustomExport/mvn-repo") {
            content {
                includeGroup("deezer.kustomexport")
            }
        }
        maven("https://cruglobal.jfrog.io/artifactory/maven-mobile/") {
            content {
                includeGroup("org.ccci.gto.android")
                includeGroup("org.cru.mobile.fork.antlr-kotlin")
                includeGroup("org.cru.mobile.fork.material-color-utilities")
            }
        }
        google()
        mavenCentral()
    }
}

includeBuild("build-logic")

include("module:analytics")
include("module:common")
include("module:interop")
include("module:parser")
include("module:parser-base")
include("module:parser-expressions")
include("module:renderer")
include("module:renderer-state")
include("module:user-activity")

include("publishing:npm")
include("publishing:spm")

// automatically accept the scans.gradle.com TOS when running in GHA
if (System.getenv("GITHUB_ACTIONS")?.toBoolean() == true) {
    extensions.findByName("gradleEnterprise")?.withGroovyBuilder {
        getProperty("buildScan").withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            setProperty("termsOfServiceAgree", "yes")
        }
    }
}
