plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    configureSdk()
    configureSourceSets()
}

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":godtools-tool-state"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.coroutines.core)

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
