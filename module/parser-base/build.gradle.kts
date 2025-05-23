plugins {
    id("godtools-shared.module-conventions")
}

android {
    namespace = "org.cru.godtools.shared.tool.parser.base"
}

kotlin {
    configureJsTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:renderer-state"))

                implementation(libs.androidx.annotation)
            }
        }
    }
}
