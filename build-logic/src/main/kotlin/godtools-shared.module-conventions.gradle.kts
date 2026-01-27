plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover")
}

android {
    compileSdkVersion(36)
    defaultConfig {
        minSdk = 24
    }
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
configureKtlint()

kover.reports {
    androidComponents.onVariants { variant ->
        variant(variant.name) {
            xml.xmlFile.set(layout.buildDirectory.file("reports/kover/${variant.name}/report.xml"))
        }
    }
}
