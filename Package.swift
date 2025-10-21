// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "GodToolsShared",
    platforms: [.iOS(.v16)],
    products: [
        .library(
            name: "GodToolsShared",
            targets: ["GodToolsShared"]
        )
    ],
    dependencies: [],
    targets: [
        .binaryTarget(
            name: "GodToolsShared",
            url: "https://cruglobal.jfrog.io/artifactory/swift-snapshots-local/cruglobal/godtoolsshared/godtoolsshared-0.0.3.zip!/CruGlobal.GodToolsShared/GodToolsShared-0.0.3.xframework.zip",
            checksum: "fe7af8f0cc21b4159894142d14b442dd8f7f618e30258c1dfba2e58efb941781"
        )
    ]
)
