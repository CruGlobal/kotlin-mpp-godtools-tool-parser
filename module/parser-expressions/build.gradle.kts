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
                implementation(kotlin("stdlib-common"))
                implementation(libs.antlr.kotlin.runtime)
                implementation(project(":module:state"))
            }
        }
    }
}

antlrKotlin {
    packageName = "org.cru.godtools.shared.tool.parser.expressions.grammar.generated"
}

// Workaround an implicit dependency issue with the antlr-kotlin tasks.
// This is related to: https://github.com/gradle/gradle/issues/25885 and https://github.com/gradle/gradle/issues/19555
tasks.whenTaskAdded {
    when (name) {
        "generateDebugAndroidTestLintModel",
        "generateDebugLintModel",
        "generateDebugUnitTestLintModel",
        "lintAnalyzeDebug",
        "lintAnalyzeDebugAndroidTest",
        "lintAnalyzeDebugUnitTest" -> {
            dependsOn("generateAndroidInstrumentedTestGrammarSource")
            dependsOn("generateAndroidUnitTestGrammarSource")
        }
    }
}
