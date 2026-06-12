plugins {
    id("godtools-shared.module-conventions")
    alias(libs.plugins.ksp)
}

kotlin {
    androidLibrary {
        namespace = "org.cru.godtools.shared.tool.parser"

        withHostTest {}
    }
    configureJsTargets()

    sourceSets {
        commonMain {
            dependencies {
                api(project(":module:parser-base"))
                api(project(":module:parser-expressions"))
                implementation(project(":module:common"))

                implementation(kotlin("stdlib"))
                implementation(libs.kotlin.coroutines.core)

                api(libs.colormath)
                api(libs.fluidLocale)
                implementation(libs.androidx.annotation)
                implementation(libs.kermit)
                implementation(libs.kustomExport)
                implementation(libs.kustomExport.coroutines)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.colormath.android.colorint)
            }
        }
        jsMain {
            dependencies {
                implementation(npm("sax", "1.2.4"))
            }
        }
        commonTest {
            dependencies {
                implementation(libs.gtoSupport.androidx.test.junit)
                implementation(libs.turbine)
            }
        }
        jsTest {
            dependencies {
                implementation(libs.goncalossilvaResources)
            }
        }
    }
}

kover.reports {
    variant("android") {
        filtersAppend {
            // exclude SaxXmlPullParser from reports because it is only used by iOS and JS
            excludes.classes("**.SaxXmlPullParser*")
        }
    }
}

// region KustomExport
dependencies.add("kspJs", libs.kustomExport.compiler)
ksp.arg("erasePackage", "true")
// endregion KustomExport
