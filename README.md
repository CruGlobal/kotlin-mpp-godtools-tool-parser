[![codecov](https://codecov.io/gh/CruGlobal/kotlin-mpp-godtools-tool-parser/branch/main/graph/badge.svg)](https://codecov.io/gh/CruGlobal/kotlin-mpp-godtools-tool-parser)

# Publishing

## SNAPSHOT builds
This project automatically publishes SNAPSHOTs to both a Maven and Cocoapods repo on any push to the `main` branch. 

## Release builds
Steps to publish a release build
- Manually trigger a GHA build on the main branch with "Release Build" checked
- Once the release build finishes there will be a "Bump version after release" PR created that should be merged
  - This PR will need to be merged to prepare for the next development iteration
