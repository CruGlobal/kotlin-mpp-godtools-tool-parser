pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:4.2.1")
            }
        }
    }
}
rootProject.name = "godtools-tool-parser"
enableFeaturePreview("VERSION_CATALOGS")
