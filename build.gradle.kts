import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    idea
    id("com.gradleup.shadow") version "8.3.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("io.freefair.lombok") version "8.13.1"
}

group = project.properties["group"]!!

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.metamechanists.org/releases") {
        name = "MetaMechanists Repository"
    }
    maven("https://repo.xenondevs.xyz/releases")
}

val coreVersion = project.properties["pylon-core.version"] as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("io.github.pylonmc:pylon-core:$coreVersion")
    implementation("org.metamechanists:DisplayModelLib:35")
    shadow("org.metamechanists:DisplayModelLib:35")
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.shadowJar {
    relocate("org.metamechanists", "${project.group}.shaded.org.metamechanists")

    mergeServiceFiles()

    archiveBaseName = project.name
    archiveClassifier = null
}

bukkit {
    name = project.properties["name"] as String
    main = project.properties["main-class"] as String
    version = project.version.toString()
    apiVersion = "1.21"
    depend = listOf("PylonCore")
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

tasks.runServer {
    downloadPlugins {
        github("pylonmc", "pylon-core", coreVersion, "pylon-core-$coreVersion.jar")
    }
    maxHeapSize = "4G"
    minecraftVersion("1.21.4")
}