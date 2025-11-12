import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project

internal fun LibraryExtension.baseConfiguration(project: Project) {
    configureSdk()
    configureCompilerOptions(project)
}

private fun BaseExtension.configureSdk() {
    compileSdkVersion(36)
    defaultConfig {
        minSdk = 24
        targetSdk = 36
    }
}

private fun BaseExtension.configureCompilerOptions(project: Project) {
    compileOptions {
        // HACK: workaround a kotlin.jvmToolchain bug
        //       see: https://issuetracker.google.com/issues/260059413
        sourceCompatibility = JavaVersion.toVersion(project.libs.findVersion("jvm").get().requiredVersion)
        targetCompatibility = JavaVersion.toVersion(project.libs.findVersion("jvm").get().requiredVersion)
    }
}
