plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

configureAndroidLibrary()
configureAntlr()
enablePublishing()

kotlin {
    configureTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(libs.antlr.kotlin.runtime)
                implementation(project(":module:state"))
            }
        }
    }
}
