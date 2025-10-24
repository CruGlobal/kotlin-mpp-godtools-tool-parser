import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform") version libs.versions.kotlin
}

kotlin {
    val xcframeworkName = "GodToolsShared"
    val xcf = XCFramework(xcframeworkName)

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {

            export(project(":module:analytics"))
            export(project(":module:interop"))
            export(project(":module:parser"))
            export(project(":module:parser-base"))
            export(project(":module:renderer-state"))
            export(project(":module:user-activity"))

            baseName = xcframeworkName

            // Specify CFBundleIdentifier to uniquely identify the framework
            binaryOption("bundleId", "org.cru.${xcframeworkName}")
            xcf.add(this)
            isStatic = false
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":module:analytics"))
                api(project(":module:interop"))
                api(project(":module:parser"))
                api(project(":module:parser-base"))
                api(project(":module:renderer-state"))
                api(project(":module:user-activity"))
            }
        }
    }
}
