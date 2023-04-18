plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    baseConfiguration(project)
}
enablePublishing()

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.findVersion("jvm").get().requiredVersion))
    }

    configureAndroidTargets()
    configureIosTargets()

    configureCommonSourceSets()
}
