plugins {
    id("godtools-shared.module-conventions")
}

kotlin {
    android {
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
