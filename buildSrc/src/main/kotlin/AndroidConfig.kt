import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion

fun LibraryExtension.baseConfiguration() {
    configureSdk()
    configureCompilerOptions()
    configureSourceSets()
}

private fun BaseExtension.configureSdk() {
    compileSdkVersion(31)
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}

private fun BaseExtension.configureCompilerOptions() {
    compileOptions {
        // HACK: workaround a kotlin.jvmToolchain bug
        //       see: https://issuetracker.google.com/issues/260059413
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

private fun BaseExtension.configureSourceSets() {
    sourceSets {
        getByName("main") {
            setRoot("src/androidMain")
        }
        getByName("test") {
            setRoot("src/androidTest")
            resources.srcDir("src/commonTest/resources")
        }
        getByName("androidTest") { setRoot("src/androidAndroidTest") }
    }
}
