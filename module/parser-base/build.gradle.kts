plugins {
    id("godtools-shared.module-conventions")
}

kotlin {
    androidLibrary {
        namespace = "org.cru.godtools.shared.tool.parser.base"

        withHostTest { }
    }

    configureJsTargets()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.annotation)
            }
        }
    }
}
