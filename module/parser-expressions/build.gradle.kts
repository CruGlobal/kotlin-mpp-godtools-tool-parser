plugins {
    id("godtools-shared.module-conventions")
    id("antlr-kotlin")
}

kotlin {
    android {
        namespace = "org.cru.godtools.shared.tool.parser.expressions"

        withHostTest { }
    }

    configureJsTargets()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libs.androidx.annotation)
                implementation(libs.antlr.kotlin.runtime)
                implementation(libs.kotlin.coroutines.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.turbine)
            }
        }
    }
}

antlrKotlin {
    packageName = "org.cru.godtools.shared.tool.parser.expressions.grammar.generated"
}
