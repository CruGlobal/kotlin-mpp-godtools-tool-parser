plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
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

koverReport {
    androidReports("release") {
        xml {
            setReportFile(layout.buildDirectory.file("reports/kover/release/report.xml"))
        }
    }
}
