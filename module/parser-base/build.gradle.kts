plugins {
    id("godtools-shared.module-conventions")
}

android {
    namespace = "org.cru.godtools.shared.tool.parser.base"
}

kotlin {
    configureJsTargets()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
    }
}
