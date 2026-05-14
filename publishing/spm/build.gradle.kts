import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    id("build-logic")
    kotlin("multiplatform")
}

val xcframeworkName = "GodToolsShared"

kotlin {
    val xcf = XCFramework(xcframeworkName)

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = xcframeworkName

            export(project(":module:analytics"))
            export(project(":module:interop"))
            export(project(":module:parser"))
            export(project(":module:parser-base"))
            export(project(":module:renderer-state"))
            export(project(":module:user-activity"))

            // Specify CFBundleIdentifier to uniquely identify the framework
            binaryOption("bundleId", "org.cru.$xcframeworkName")
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
configureKtlint()

val zipXCFramework = tasks.register<Zip>("zip${xcframeworkName}SpmXCFramework") {
    dependsOn("assemble${xcframeworkName}XCFramework")

    from(layout.buildDirectory.dir("XCFrameworks/release"))
    include("$xcframeworkName.xcframework/**")
    archiveFileName.set("$xcframeworkName-${project.version}.xcframework.zip")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/spm"))
}

val generateSpmPackageSwift = tasks.register("generate${xcframeworkName}SpmPackageSwift") {
    val zipFile = zipXCFramework.flatMap { it.archiveFile }
    val outputFile = layout.buildDirectory.file("outputs/spm/Package.swift")
    inputs.files(zipFile)
    outputs.file(outputFile)

    doLast {
        val zipFileName = zipFile.get().asFile.name
        outputFile.get().asFile.writeText(
            """
            // swift-tools-version: 6.2
            // The swift-tools-version declares the minimum version of Swift required to build this package.

            import PackageDescription

            let package = Package(
              name: "$xcframeworkName",
              platforms: [
                .iOS(.v16),
              ],
              products: [
                  .library(name: "$xcframeworkName", targets: ["$xcframeworkName"])
              ],
              dependencies: [],
              targets: [
                  .binaryTarget(
                    name: "$xcframeworkName",
                    path: "$zipFileName"
                  )
              ]
            )
            """.trimIndent()
        )
    }
}

tasks.register("assemble${xcframeworkName}SpmPackage") {
    group = "build"
    description = "Assembles the $xcframeworkName SPM package."
    dependsOn(generateSpmPackageSwift)
}
