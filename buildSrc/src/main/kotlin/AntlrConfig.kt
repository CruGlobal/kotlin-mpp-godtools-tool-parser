import com.strumenta.antlrkotlin.gradleplugin.AntlrKotlinTask
import org.gradle.api.Project
import org.gradle.api.plugins.antlr.AntlrPlugin.ANTLR_CONFIGURATION_NAME
import org.gradle.api.plugins.antlr.internal.AntlrSourceVirtualDirectoryImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File

fun Project.configureAntlr() {
    // create antlr configuration
    val antlrConfiguration = configurations.create(ANTLR_CONFIGURATION_NAME)
        .setVisible(false)
        .setDescription("The Antlr libraries to be used for this project.")

    // for each source set
    extensions.getByType(KotlinMultiplatformExtension::class.java).sourceSets.all {
        // 1) Add a new 'antlr' virtual directory mapping
        val antlrDirectoryDelegate = AntlrSourceVirtualDirectoryImpl(name, objects)
        antlrDirectoryDelegate.antlr.srcDir("src/$name/antlr")

        // 2) Create task to generate the ANTLR grammar parsers
        val generateTask = tasks.register("generate${name.capitalize()}GrammarSource", AntlrKotlinTask::class.java) {
            antlrClasspath = antlrConfiguration
            maxHeapSize = "64m"
            arguments = listOf("-visitor")
            source = antlrDirectoryDelegate.antlr
            outputDirectory = File(buildDir, "generated/antlr/src/${this@all.name}/kotlin")
        }

        // 3) Set up the Antlr output directory (adding to javac inputs!)
        kotlin.srcDir(generateTask.map { it.outputDirectory })
    }
}
