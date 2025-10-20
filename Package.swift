// swift-tools-version: 5.7
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
  name: "GodToolsShared",
  platforms: [
    .iOS(.v16),
  ],
  products: [
      .library(name: "GodToolsShared", targets: ["GodToolsShared"])
  ],
  targets: [
      .binaryTarget(
        name: "GodToolsShared",
        url: "<link to the uploaded XCFramework ZIP file>",
        checksum: "")
  ]
)