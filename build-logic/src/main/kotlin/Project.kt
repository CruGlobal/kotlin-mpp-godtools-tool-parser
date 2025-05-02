import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal fun Project.ktlint(action: KtlintExtension.() -> Unit) = extensions.configure(action)

internal val Project.libs get() = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
