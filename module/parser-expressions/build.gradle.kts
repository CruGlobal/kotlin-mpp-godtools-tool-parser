plugins {
    id("godtools-shared.module-conventions")
    id("antlr-kotlin")
}

android {
    namespace = "org.cru.godtools.shared.tool.parser.expressions"
}

kotlin {
    configureJsTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:parser-base"))

                implementation(kotlin("stdlib-common"))
                implementation(libs.antlr.kotlin.runtime)
            }
        }
    }
}

antlrKotlin {
    packageName = "org.cru.godtools.shared.tool.parser.expressions.grammar.generated"
}
