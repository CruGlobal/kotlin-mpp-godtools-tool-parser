// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "GodToolsShared",
    products: [
        // Products define the executables and libraries a package produces, making them visible to other packages.
        .library(
            name: "GodToolsShared",
            targets: ["GodToolsShared", "GodToolsSharedBinaryPackage"]),
    ],
    targets: [
        // Targets are the basic building blocks of a package, defining a module or a test suite.
        // Targets can depend on other targets in this package and products from dependencies.
        .target(
            name: "GodToolsShared"),
        .testTarget(
            name: "GodToolsSharedTests",
            dependencies: ["GodToolsShared"]),
        .binaryTarget(name: "GodToolsSharedBinaryPackage", path: "build/XCFrameworks/release/kotlin_mpp_godtools_tool_parser.xcframework")
    ]
)
