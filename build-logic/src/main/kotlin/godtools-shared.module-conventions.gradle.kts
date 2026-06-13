plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("org.jetbrains.kotlinx.kover")
}

enablePublishing()

kotlin {
    android {
        compileSdk = 36
        minSdk = 24
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

        optionalAndroidHostTest {
            dependencies {
                implementation(project.versionCatalog.findBundle("android-test-framework").get())
            }
        }
    }
}
configureKtlint()

kover.reports {
    variant("android") {
        xml.xmlFile.set(layout.buildDirectory.file("reports/kover/android/report.xml"))
    }
}
