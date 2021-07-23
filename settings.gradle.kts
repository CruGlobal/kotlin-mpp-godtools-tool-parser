pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:4.2.2")
            }
        }
    }
}
rootProject.name = "GodtoolsToolParser"
enableFeaturePreview("VERSION_CATALOGS")

include("godtools-tool-parser")
