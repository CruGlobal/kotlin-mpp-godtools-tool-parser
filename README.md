[![codecov](https://codecov.io/gh/CruGlobal/kotlin-mpp-godtools-tool-parser/branch/main/graph/badge.svg)](https://codecov.io/gh/CruGlobal/kotlin-mpp-godtools-tool-parser)

# Releases

## SNAPSHOT builds
This project automatically publishes SNAPSHOTs to both a Maven and Cocoapods repo on any push to the `main` branch. 

## Release builds
Steps to release a release build
- Create a git tag that points at main.
  - The name of this tag should be `v{release_version}`, with `release_version` matching the version specified in the `gradle.properties` file
  - This will trigger a GitHub Actions workflow that will publish artifacts in the correct locations
- Increment the version found in `gradle.properties` on the `main` branch
  - This will prepare the repo for the next development iteration, and prevents SNAPSHOTS being published for the previous release version.
