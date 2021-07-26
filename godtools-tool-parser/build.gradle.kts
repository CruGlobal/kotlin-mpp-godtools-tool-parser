plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

kotlin {
    android {
        publishLibraryVariants("debug", "release")
    }
    configureIosTargets()
    configureJsTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

                implementation(libs.colormath)
                implementation(libs.fluidLocale)
                implementation(libs.napier)
                implementation(libs.splitties.bitflags)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("androidx.test.ext:junit:1.1.3")
                implementation("org.robolectric:robolectric:4.6.1")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("sax", "1.2.4"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(libs.okio.js)
                implementation(libs.okio.nodefilesystem)
            }
        }
    }
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    sourceSets {
        val main by getting { setRoot("src/androidMain") }
        val test by getting {
            setRoot("src/androidTest")
            resources.srcDir("src/commonTest/resources")
        }
        val androidTest by getting { setRoot("src/androidAndroidTest") }
    }
}
