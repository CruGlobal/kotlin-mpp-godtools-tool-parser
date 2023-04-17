import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask
import org.gradle.api.Project
import org.gradle.api.plugins.antlr.AntlrPlugin.ANTLR_CONFIGURATION_NAME
import org.gradle.api.plugins.antlr.AntlrSourceDirectorySet
import org.gradle.api.plugins.antlr.internal.DefaultAntlrSourceDirectorySet
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

/**
 * Logic based off of AntlrPlugin.apply()
 *
 * see: [org.gradle.api.plugins.antlr.AntlrPlugin]
 */
fun Project.configureAntlr() {
    // create antlr configuration
    val antlrConfiguration = configurations.create(ANTLR_CONFIGURATION_NAME)
        .setVisible(false)
        .setDescription("The Antlr libraries to be used for this project.")

    // for each source set
    extensions.getByType(KotlinMultiplatformExtension::class.java).sourceSets.all {
        // 1) Create Antlr source set
        val antlrSourceSet = objects.newInstance(
            DefaultAntlrSourceDirectorySet::class.java,
            objects.sourceDirectorySet("$name.antlr", "$displayName Antlr source"),
        )
        antlrSourceSet.filter.include("**/*.g")
        antlrSourceSet.filter.include("**/*.g4")
        antlrSourceSet.srcDir("src/$name/antlr")

        // 2) Create Antlr generate parser task
        val generateTask = tasks.register("generate${name.capitalize()}GrammarSource", AntlrKotlinTask::class.java) {
            antlrClasspath = antlrConfiguration
            maxHeapSize = "64m"
            arguments = listOf("-visitor")
            source = antlrSourceSet
            outputDirectory = File(buildDir, "generated/antlr/src/${this@all.name}/kotlin")
        }

        // 3) Set up the Antlr output directory (adding to javac inputs!)
        kotlin.srcDir(generateTask.map { it.outputDirectory })
    }
}
