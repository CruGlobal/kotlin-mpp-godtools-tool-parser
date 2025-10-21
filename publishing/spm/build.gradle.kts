import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform") version libs.versions.kotlin
    id("store.kmpd.plugin") version "0.0.5"
}

kotlin {
    val xcframeworkName = "GodToolsShared"
    val xcf = XCFramework(xcframeworkName)

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = xcframeworkName

            // Specify CFBundleIdentifier to uniquely identify the framework
            binaryOption("bundleId", "org.cru.${xcframeworkName}")
            xcf.add(this)
            isStatic = false
        }
    }

    appleBinariesDeployment {

    }
}
