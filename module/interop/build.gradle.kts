plugins {
    id("godtools-shared.module-conventions")
}

kotlin {
    androidLibrary {
        namespace = "org.cru.godtools.shared.interop"

        withHostTest { }
    }

    sourceSets {
        iosMain {
            dependencies {
                implementation(project(":module:common"))

                implementation(libs.kermit)
            }
        }
    }
}
