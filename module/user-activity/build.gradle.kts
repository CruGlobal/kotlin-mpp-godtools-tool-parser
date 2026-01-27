plugins {
    id("godtools-shared.module-conventions")
}

android {
    namespace = "org.cru.godtools.shared.user.activity"
}

kotlin {
    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.materialColorUtilities)
            }
        }
        commonMain {
            dependencies {
                api(project(":module:common"))

                implementation(libs.androidx.annotation)
                implementation(libs.colormath)
                implementation(libs.fluidLocale)
                implementation(libs.gtoSupport.fluidsonic.locale)
                implementation(libs.okio)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.gtoSupport.androidx.test.junit)
            }
        }
    }
}
