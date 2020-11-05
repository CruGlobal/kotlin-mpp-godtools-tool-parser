plugins {
    kotlin("multiplatform") version "1.4.10"
    id("com.android.library")
    id("maven-publish")
}

group = "org.cru.godtools.kotlin"
version = "0.1.0-SNAPSHOT"

repositories {
    google()
    jcenter()
    mavenCentral()
}
kotlin {
    android {
        publishLibraryVariants("release")
    }
    iosX64("ios") {
        binaries {
            framework {
                baseName = "godtools-tool-parser"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosMain by getting
        val iosTest by getting
    }
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(30)
    }
}
