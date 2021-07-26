import com.android.build.gradle.BaseExtension

fun BaseExtension.configureSdk() {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }
}

fun BaseExtension.configureSourceSets() {
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
