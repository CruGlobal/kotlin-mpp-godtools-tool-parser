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
        checksum: "6fe8b6e3a790a85c344257fba1a1839099678860f4ecfa75d619486f6278e1bc")
  ]
)