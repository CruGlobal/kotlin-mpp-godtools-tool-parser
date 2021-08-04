plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

configureAndroidLibrary()
enablePublishing()

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:state"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.coroutines.core)

                implementation(libs.colormath)
                implementation(libs.fluidLocale)
                implementation(libs.napier)
                implementation(libs.splitties.bitflags)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
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
