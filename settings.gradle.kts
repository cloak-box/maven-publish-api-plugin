plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "maven-publish-api-plugin"

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("io.github.cloak-box.plugin:maven-api-plugin:1.0.0.2")
    }
}