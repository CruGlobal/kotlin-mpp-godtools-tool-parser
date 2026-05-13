plugins {
    id("build-logic")
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

configureKtlint()
