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
                api(project(":module:parser-base"))
                implementation(project(":module:common"))
                implementation(project(":module:parser-expressions"))

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
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.colormath.android.colorint)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("sax", "1.2.4"))
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

kover.reports {
    androidComponents.onVariants { variant ->
        variant(variant.name) {
            filtersAppend {
                // exclude SaxXmlPullParser from reports because it is only used by iOS and JS
                excludes.classes("**.SaxXmlPullParser*")
            }
        }
    }
}

// region KustomExport
dependencies.add("kspJs", libs.kustomExport.compiler)
ksp.arg("erasePackage", "true")
// endregion KustomExport
