plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "org.cru.godtools.shared.renderer"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:parser"))

                implementation(compose.runtime)
                implementation(compose.material3)
            }
        }
    }
}
