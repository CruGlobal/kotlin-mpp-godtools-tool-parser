plugins {
    id("godtools-shared.module-conventions")
}

android {
    namespace = "org.cru.godtools.shared.analytics"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
    }
}
