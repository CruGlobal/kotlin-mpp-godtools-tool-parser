import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension

fun LibraryExtension.baseConfiguration() {
    configureSdk()
    configureSourceSets()
}

private fun BaseExtension.configureSdk() {
    compileSdkVersion(31)
    defaultConfig {
        minSdk = 21
        targetSdk = 31
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
