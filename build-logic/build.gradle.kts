plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

kotlin.jvmToolchain {
    languageVersion.set(libs.versions.jvm.map { JavaLanguageVersion.of(it) })
}

gradlePlugin {
    plugins.register("antlr-kotlin") {
        id = "antlr-kotlin"
        implementationClass = "org.cru.godtools.shared.gradle.KotlinAntlrPlugin"
    }
    plugins.register("build-logic") {
        id = "build-logic"
        implementationClass = "org.cru.godtools.shared.gradle.BuildLogicPlugin"
    }
}

repositories {
    google()
    maven("https://cruglobal.jfrog.io/artifactory/maven-mobile/") {
        content {
            includeGroup("org.cru.mobile.fork.antlr-kotlin")
        }
    }
    mavenCentral()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlin.kover.gradle)
    implementation(libs.antlr.kotlin.gradle)
}
