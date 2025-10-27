import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import store.kmpd.HttpStorageDeployment
import store.kmpd.spm.SPMPackageDeployment
import store.kmpd.spm.SPMXCFrameworkDeployment
import store.kmpd.spm.SwiftPackageConfiguration

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

    appleBinariesDeployment {
        deployAsSwiftPackage(
            swiftPackageConfiguration = SwiftPackageConfiguration(
                packageDeployment = SPMPackageDeployment.GitDeployment(
                    repository = "https://github.com/CruGlobal/godtools-shared-spm-godtools-shared-swift.git"
                ),
            ),
            xcframeworkDeployment = SPMXCFrameworkDeployment.HttpDeployment(
                deployment = HttpStorageDeployment.Upload(
                    username = findProperty("cruGlobalMavenRepositoryUsername")?.toString().orEmpty(),
                    password = findProperty("cruGlobalMavenRepositoryPassword")?.toString().orEmpty(),
                    uploadDirectoryUrl = "https://cruglobal.jfrog.io/artifactory/swift-snapshots-local"
                )
            )
        )
    }
}
