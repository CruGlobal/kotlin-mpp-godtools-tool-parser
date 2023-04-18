plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.tool.parser.expressions"
}
configureAntlr()

kotlin {
    configureJsTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libs.antlr.kotlin.runtime)
                implementation(project(":module:state"))
            }
        }
    }
}
