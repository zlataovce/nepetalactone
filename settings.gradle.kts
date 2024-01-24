pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "nepetalactone"

fun includeNamespaced(vararg paths: String) {
    paths.forEach { path ->
        include("${rootProject.name}-$path")
    }
}

includeNamespaced("api", "server")
