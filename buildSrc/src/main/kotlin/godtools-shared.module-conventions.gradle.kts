plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    baseConfiguration()
}
enablePublishing()

kotlin {
    configureAndroidTargets()
    configureIosTargets()

    configureCommonSourceSets()
}
