import org.gradle.api.Project

fun Project.configureKtlint() {
    pluginManager.apply("org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set(versionCatalog.findVersion("ktlint").get().requiredVersion)

        dependencies.add("ktlintRuleset", versionCatalog.findBundle("ktlint-rulesets").get())

        filter {
            exclude { layout.buildDirectory.asFileTree.contains(it.file) }
        }
    }
}
