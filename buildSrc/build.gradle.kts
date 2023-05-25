plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io") {
        content {
            includeGroup("com.strumenta.antlr-kotlin")
        }
    }
    maven("https://cruglobal.jfrog.io/artifactory/maven-mobile/") {
        content {
            includeGroup("org.cru.mobile.fork.antlr-kotlin")
        }
    }
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlin.kover.gradle)
    implementation(libs.antlr.kotlin.gradle)
}

kotlin.jvmToolchain {
    languageVersion.set(libs.versions.jvm.map { JavaLanguageVersion.of(it) })
}
