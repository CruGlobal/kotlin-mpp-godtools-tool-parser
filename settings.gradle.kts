pluginManagement {
    repositories {
        maven("https://jitpack.io") {
            content {
                includeGroup("com.strumenta.antlr-kotlin")
            }
        }
        google()
        gradlePluginPortal()
    }
}

rootProject.name = "GodtoolsToolParser"
enableFeaturePreview("VERSION_CATALOGS")

include("godtools-tool-parser")
include("module:state")
include("module:expressions")

include("test-fixtures")
