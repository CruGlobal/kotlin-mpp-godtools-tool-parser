import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.targets.js.npm.NpmDependency

open class NpmDependencies internal constructor(project: Project) {
    val sax by lazy {
        NpmDependency(
            project = project,
            name = "sax",
            version = "1.2.4",
        )
    }
}

val Project.npmLibs: NpmDependencies get() = extensions.findByType() ?: extensions.create("npmLibs", this)
