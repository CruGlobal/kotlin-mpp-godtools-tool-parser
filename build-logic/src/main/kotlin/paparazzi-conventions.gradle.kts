plugins {
    id("app.cash.paparazzi")
}

// This task only exists in modules that apply paparazzi, so running it unqualified generates kover coverage
// reports for paparazzi test runs without triggering unit tests in other modules.
tasks.register("koverXmlReportPaparazzi") {
    group = "verification"
    dependsOn("koverXmlReport")
}

// HACK: Paparazzi tests are excluded from testAndroidHostTest by default because layoutlib and Robolectric
//       conflict when run in the same JVM process. Pass -Ppaparazzi to include them. Remove this once
//       resolved: https://github.com/cashapp/paparazzi/issues/1979
val paparazziEnabled = project.hasProperty("paparazzi")
val paparazziCategory = "org.cru.godtools.shared.renderer.BasePaparazziTest"

tasks.withType<Test> {
    if (name != "testAndroidHostTest") return@withType
    useJUnit {
        if (paparazziEnabled) includeCategories(paparazziCategory) else excludeCategories(paparazziCategory)
    }
}
