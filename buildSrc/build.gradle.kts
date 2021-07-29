plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.plugin)
}
