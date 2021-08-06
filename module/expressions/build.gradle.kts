import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask

buildscript {
    dependencies {
        classpath(libs.antlr.kotlin.gradle)
    }
}

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

configureAndroidLibrary()
enablePublishing()

val commonMainAntlrTask = tasks.register<AntlrKotlinTask>("generateCommonMainAntlrSource") {
    antlrClasspath = configurations.detachedConfiguration(
        project.dependencies.create(libs.antlr.kotlin.target.get())
    )
    maxHeapSize = "64m"
    packageName = "org.cru.godtools.expressions.internal.grammar"
    arguments = listOf("-visitor")
    source = project.objects
        .sourceDirectorySet("antlr", "antlr")
        .srcDir("src/commonMain/antlr").apply {
            include("*.g4")
        }
    outputDirectory = File(buildDir, "generated-src/antlr/commonMain")
}

kotlin {
    configureTargets()

    sourceSets {
        // TODO: is there a better way to attached generated sources to the commonMain source set?
        val generatedCommonMain by creating {
            // add antlr generated sources
            kotlin.srcDir(commonMainAntlrTask.map { it.outputDirectory })

            dependencies {
                api(kotlin("stdlib-common"))
                api(libs.antlr.kotlin.runtime)
            }
        }

        val commonMain by getting {
            dependsOn(generatedCommonMain)

            dependencies {
                implementation(project(":module:state"))
            }
        }
    }
}

// disable the ktlint tasks for generated sources
tasks.findByName("ktlintGeneratedCommonMainSourceSetCheck")?.enabled = false
tasks.findByName("ktlintGeneratedCommonMainSourceSetFormat")?.enabled = false

// tasks.getByName("compileKotlinJvm").dependsOn("generateKotlinCommonGrammarSource")
