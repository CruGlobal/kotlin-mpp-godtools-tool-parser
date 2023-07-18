plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)
    implementation(libs.kotlin.kover.gradle)
}

kotlin.jvmToolchain {
    languageVersion.set(libs.versions.jvm.map { JavaLanguageVersion.of(it) })
}
