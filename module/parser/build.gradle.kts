plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlin.kover)
}

android {
    namespace = "org.cru.godtools.shared.tool.parser"
    baseConfiguration()
}
enablePublishing()

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:state"))
                implementation(project(":module:parser-expressions"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.coroutines.core)

                implementation(libs.colormath)
                implementation(libs.fluidLocale)
                implementation(libs.gtoSupport.androidx.annotation)
                implementation(libs.gtoSupport.fluidsonic.locale)
                implementation(libs.napier)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.colormath.android.colorint)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("sax", "1.2.4"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.gtoSupport.androidx.test.junit)
                implementation(libs.turbine)
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
