plugins {
    kotlin("jvm") version "1.8.21"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.mcsnapix.${project.name.toLowerCase()}"
version = "0.0.3"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.26")

    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("co.aikar:idb-core:1.0.0-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.1")
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.2.1")
}

val libs = "${project.group}.lib"

fun com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.relocateDependency(pkg: String) {
    relocate(pkg, "$libs.$pkg")
}


tasks {
    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    processResources {
        filteringCharset = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("${project.name}.jar")

        relocateDependency("org.jetbrains")
        relocateDependency("org.intellij")
        relocateDependency("kotlin")
        relocateDependency("space.arim.dazzleconf")
        relocateDependency("net.kyori")
        relocateDependency("co.aikar.idb")

        dependencies {
            exclude("META-INF/NOTICE")
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
            exclude("META-INF/**.kotlin_module")
        }

        minimize()
    }
}

bukkit {
    main = "${project.group}.${project.name}"
    authors = listOf("SnapiX")
    depend = listOf("Vault")
}