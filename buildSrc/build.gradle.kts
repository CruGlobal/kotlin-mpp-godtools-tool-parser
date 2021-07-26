plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    implementation("com.android.tools.build:gradle:4.2.2")
    implementation(libs.kotlin.plugin)
}
