plugins {
    id("godtools-shared.module-conventions")
}

android {
    namespace = "org.cru.godtools.shared.interop"
}

kotlin {
    sourceSets {
        iosMain {
            dependencies {
                implementation(project(":module:common"))

                implementation(libs.kermit)
            }
        }
    }
}
