// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "GodToolsShared",
    platforms: [
        .iOS(.v14)
    ],
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "GodToolsShared",
            targets: ["GodToolsSharedBinaryPackage"]),
    ],
    targets: [
        .binaryTarget(name: "GodToolsSharedBinaryPackage", path: "build/XCFrameworks/release/kotlin_mpp_godtools_tool_parser.xcframework")
    ]
)
