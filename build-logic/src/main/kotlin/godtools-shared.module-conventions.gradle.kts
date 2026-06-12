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
    androidTarget {
        publishLibraryVariants("debug", "release")
    }
    configureIosTargets()

    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(versionCatalog.findVersion("jvm").get().requiredVersion))
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project.versionCatalog.findBundle("common-test-framework").get())
            }
        }

        androidUnitTest {
            dependencies {
                implementation(project.versionCatalog.findBundle("android-test-framework").get())
            }
        }
    }
}
configureKtlint()

kover.reports {
    androidComponents.onVariants { variant ->
        variant(variant.name) {
            xml.xmlFile.set(layout.buildDirectory.file("reports/kover/${variant.name}/report.xml"))
        }
    }
}
