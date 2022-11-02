rootProject.name = "GodtoolsToolParser"

dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io") {
            content {
                includeGroup("com.strumenta.antlr-kotlin")
            }
        }
        maven("https://cruglobal.jfrog.io/artifactory/maven-mobile/") {
            content {
                includeGroup("org.ccci.gto.android")
                includeGroup("org.cru.mobile.fork.antlr-kotlin")
            }
        }
        google()
        mavenCentral()
    }
}

include("module:analytics")
include("module:common")
include("module:parser")
include("module:parser-expressions")
include("module:state")
include("module:user-activity")

// automatically accept the scans.gradle.com TOS when running in GHA
if (System.getenv("GITHUB_ACTIONS")?.toBoolean() == true) {
    extensions.findByName("gradleEnterprise")?.withGroovyBuilder {
        getProperty("buildScan").withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            setProperty("termsOfServiceAgree", "yes")
        }
    }
}
