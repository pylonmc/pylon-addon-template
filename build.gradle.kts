import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

// Specify what plugins to use
plugins {
    java
    // IntelliJ plugin
    idea
    // used to generate plugin.yml
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    // used to run a test server locally
    id("xyz.jpenilla.run-paper") version "2.3.0"
    // lombok (provides useful annotations)
    id("io.freefair.lombok") version "8.13.1"
}

// Specify the 'group' (eg: io.github.pylonmc.myaddon)
group = project.properties["group"]!!

// Add repositories from which to download dependencies
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.xenondevs.xyz/releases")
}

// Get dependency versions from gradle.properties
val coreVersion = project.properties["pylon-core.version"] as String
val baseVersion = project.properties["pylon-base.version"] as String

// Download dependencies
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("io.github.pylonmc:pylon-core:$coreVersion")
    compileOnly("io.github.pylonmc:pylon-base:$baseVersion")
}

// Settings for IntelliJ
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    // Use Java 21
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

// Generate the plugin.yml file using the bukkit gradle plugin
bukkit {
    name = project.properties["name"] as String
    main = project.properties["main-class"] as String
    version = project.version.toString()
    apiVersion = "1.21"
    depend = listOf("PylonCore", "PylonBase")
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

// Run a server using the run server gradle plugin
tasks.runServer {
    doFirst {
        // Remove the plugins folder. This is so any changes to language files etc are propagated.
        val runFolder = project.projectDir.resolve("run")
        val pluginsDir = runFolder.resolve("plugins")
        if (!System.getProperty("io.github.pylonmc.pylon.disableConfigReset").toBoolean()) {
            pluginsDir.deleteRecursively()
        }
    }

    // Download pylon core and add it to the plugins folder
    downloadPlugins {
        github("pylonmc", "pylon-core", coreVersion, "pylon-core-$coreVersion.jar")
    }

    // Download pylon base and add it to the plugins folder
    downloadPlugins {
        github("pylonmc", "pylon-base", baseVersion, "pylon-base-$baseVersion.jar")
    }

    maxHeapSize = "4G"
    minecraftVersion("1.21.8")
}