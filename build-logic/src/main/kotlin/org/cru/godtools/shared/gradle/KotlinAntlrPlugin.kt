package org.cru.godtools.shared.gradle

import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.antlr.AntlrPlugin.ANTLR_CONFIGURATION_NAME
import org.gradle.api.plugins.antlr.internal.DefaultAntlrSourceDirectorySet
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

/**
 * Logic based off of AntlrPlugin.apply()
 *
 * see: [org.gradle.api.plugins.antlr.AntlrPlugin]
 */
class KotlinAntlrPlugin : Plugin<Project> {
    private lateinit var extension: KotlinAntlrExtension

    override fun apply(target: Project) {
        extension = target.extensions.create("antlrKotlin")

        // create antlr configuration
        val antlrConfiguration = target.configurations.create(ANTLR_CONFIGURATION_NAME)
            .setVisible(false)
            .setDescription("The Antlr libraries to be used for this project.")

        // for each source set
        target.extensions.getByType(KotlinMultiplatformExtension::class.java).sourceSets.configureEach {
            // 1) Create Antlr source set
            val antlrSourceSet = target.objects.newInstance(
                DefaultAntlrSourceDirectorySet::class.java,
                target.objects.sourceDirectorySet("$name.antlr", "$name Antlr source"),
            )
            antlrSourceSet.filter.include("**/*.g")
            antlrSourceSet.filter.include("**/*.g4")
            antlrSourceSet.srcDir("src/$name/antlr")

            // 2) Create Antlr generate parser task
            val generateTask =
                target.tasks.register("generate${name.capitalize()}GrammarSource", AntlrKotlinTask::class.java) {
                    antlrClasspath = antlrConfiguration
                    maxHeapSize = "64m"
                    arguments = listOf("-visitor")
                    source = antlrSourceSet
                    packageName = extension.packageName.apply { finalizeValue() }.get()
                    outputDirectory = target.layout.buildDirectory
                        .dir("generated-src/antlr/src/${this@configureEach.name}/kotlin")
                        .get().asFile
                }

            // 3) Set up the Antlr output directory (adding to javac inputs!)
            // TODO: register as a generated source once it is supported in the gradle plugin
            //       see: https://youtrack.jetbrains.com/issue/KT-45161#focus=Comments-27-8632322.0-0
            kotlin.srcDir(generateTask)
        }
    }
}
