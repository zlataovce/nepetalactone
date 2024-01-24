plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("io.papermc.paperweight.patcher") version "1.5.11"
    `maven-publish`
    java
}

allprojects {
    group = "me.kcra.nepetalactone"
    version = "1.20.4-R0.1-SNAPSHOT"
    description = "A Paper fork with features no one asked for."
}

val paperMavenRepo = "https://repo.papermc.io/repository/maven-public/"

repositories {
    mavenCentral()
    maven(paperMavenRepo) {
        content {
            onlyForConfigurations(configurations.paperclip.name)
        }
    }
}

dependencies {
    // keep in sync with upstream
    remapper("net.fabricmc:tiny-remapper:0.8.10:fat")
    decompiler("net.minecraftforge:forgeflower:2.0.627.2")
    paperclip("io.papermc:paperclip:3.0.3")
}

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
        maven(paperMavenRepo)
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = Charsets.UTF_8.name()
            options.release = 17
        }
        withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
        withType<ProcessResources> {
            filteringCharset = Charsets.UTF_8.name()
        }
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
}

paperweight {
    serverProject = project(":${rootProject.name}-server")

    remapRepo = paperMavenRepo
    decompileRepo = paperMavenRepo

    usePaperUpstream(providers.gradleProperty("paperRef")) {
        withPaperPatcher {
            apiPatchDir = layout.projectDirectory.dir("patches/api")
            apiOutputDir = layout.projectDirectory.dir("${rootProject.name}-api")

            serverPatchDir = layout.projectDirectory.dir("patches/server")
            serverOutputDir = layout.projectDirectory.dir("${rootProject.name}-server")
        }
        patchTasks.register("generatedApi") {
            isBareDirectory = true
            upstreamDirPath = "paper-api-generator/generated"
            patchDir = layout.projectDirectory.dir("patches/generatedApi")
            outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
        }
    }
}

tasks.generateDevelopmentBundle {
    apiCoordinates = "${rootProject.group}:${rootProject.name}-api"
    mojangApiCoordinates = "io.papermc.paper:paper-mojangapi"
    libraryRepositories = listOf("https://repo.maven.apache.org/maven2/", paperMavenRepo)
}

publishing {
    publications {
        create<MavenPublication>("devBundle") {
            artifact(tasks.generateDevelopmentBundle) {
                artifactId = "dev-bundle"
            }
        }
    }
}
