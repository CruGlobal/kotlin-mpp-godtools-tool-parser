plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.ksp)
}

android {
    namespace = "org.cru.godtools.shared.tool.parser"

    sourceSets {
        getByName("test") {
            resources.srcDir("src/commonTest/resources")
        }
    }
}

kotlin {
    configureJsTargets()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":module:state"))
                implementation(project(":module:common"))
                implementation(project(":module:parser-expressions"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.coroutines.core)

                implementation(libs.colormath)
                implementation(libs.fluidLocale)
                implementation(libs.gtoSupport.androidx.annotation)
                implementation(libs.gtoSupport.fluidsonic.locale)
                implementation(libs.kustomExport)
                implementation(libs.kustomExport.coroutines)
                implementation(libs.napier)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.colormath.android.colorint)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npmLibs.sax)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.gtoSupport.androidx.test.junit)
                implementation(libs.turbine)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(libs.goncalossilvaResources)
            }
        }
    }
}

koverReport {
    androidReports("release") {
        xml {
            filters {
                excludes {
                    // exclude SaxXmlPullParser from reports because it is only used by iOS and JS
                    // TODO: remove this if we ever support coverage reports for iOS or js
                    classes("**.SaxXmlPullParser*")
                }
            }
        }
    }
}

// region KustomExport
dependencies.add("kspJs", libs.kustomExport.compiler)
ksp.arg("erasePackage", "true")
// endregion KustomExport
