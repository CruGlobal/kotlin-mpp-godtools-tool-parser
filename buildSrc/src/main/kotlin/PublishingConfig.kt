import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.credentials

fun Project.enablePublishing() {
    apply(plugin = "maven-publish")
    with(extensions.getByType(PublishingExtension::class.java)) {
        repositories {
            maven {
                name = "cruGlobalMavenRepository"
                setUrl(
                    when {
                        isSnapshotVersion ->
                            "https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-snapshots-local/"
                        else -> "https://cruglobal.jfrog.io/cruglobal/list/maven-cru-android-public-releases-local/"
                    }
                )

                credentials(PasswordCredentials::class)
            }
        }
    }
}
