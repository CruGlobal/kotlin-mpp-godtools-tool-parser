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
}

repositories {
    maven("https://cruglobal.jfrog.io/artifactory/maven-mobile/") {
        content {
            includeGroup("org.cru.mobile.fork.antlr-kotlin")
        }
    }
    mavenCentral()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.kotlin.gradle)
    implementation(libs.antlr.kotlin.gradle)
}
