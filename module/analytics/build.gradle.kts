plugins {
    id("godtools-shared.module-conventions")
}

kotlin {
    androidLibrary {
        namespace = "org.cru.godtools.shared.analytics"

        withHostTest { }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
    }
}
