plugins {
    kotlin("multiplatform") version "1.5.20" apply false
    kotlin("native.cocoapods") version "1.5.20" apply false
    id("com.android.library") apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.5.20" apply false
    id("org.ajoberstar.grgit") version "4.1.0" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("com.vanniktech.android.junit.jacoco") version "0.16.0"
}

group = "org.cru.godtools.kotlin"
version = "0.2.0-SNAPSHOT"

val isSnapshotVersion get() = version.toString().endsWith("-SNAPSHOT")

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter {
            content {
                includeGroup("com.louiscad.splitties")
            }
        }
    }
}

// region Jacoco
junitJacoco {
    jacocoVersion = libs.versions.jacoco.get()
    includeNoLocationClasses = true
    excludes = listOf(
        // we exclude SaxXmlPullParser from reports because it is only used by iOS and JS
        "**/SaxXmlPullParser*"
    )
}
allprojects {
    apply(plugin = "org.gradle.jacoco")
    tasks.withType(Test::class.java) {
        extensions.configure(JacocoTaskExtension::class.java) {
            excludes = excludes.orEmpty() + "jdk.internal.*"
        }
    }
    tasks.create("jacocoTestReport") {
        dependsOn(tasks.withType(JacocoReport::class.java))
    }
}
// endregion Jacoco

// region KtLint
ktlint {
    version.set(libs.versions.ktlint)
}
// endregion KtLint
