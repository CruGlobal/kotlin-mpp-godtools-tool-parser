import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.credentials

internal fun Project.enablePublishing() {
    plugins.apply("maven-publish")
    with(extensions.getByType(PublishingExtension::class.java)) {
        repositories {
            maven {
                name = "cruGlobalMavenRepository"
                when {
                    isSnapshotVersion ->
                        setUrl("https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-snapshots-local/")

                    else -> setUrl("https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-releases-local/")
                }
                credentials(PasswordCredentials::class)
            }
        }
    }
}
