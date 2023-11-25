import com.jetbrains.exposed.gradle.plugin.shadowjar.kotlinRelocate

plugins {
    kotlin("jvm") version "1.8.21"
    id("com.jetbrains.exposed.gradle.plugin") version "0.2.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "ru.mcsnapix.${project.name.toLowerCase()}"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
    maven("https://repo.alessiodp.com/releases/")
}

val exposedVersion: String by project
dependencies {
    api("net.kyori:adventure-text-minimessage:4.14.0")
    api("net.kyori:adventure-platform-bukkit:4.3.1")

    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.alessiodp.lastloginapi:lastloginapi-api:1.7.2")
    compileOnly("net.luckperms:api:5.4")

    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("space.arim.dazzleconf:dazzleconf-ext-snakeyaml:1.2.1") {
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.0") {
        exclude("com.github.waffle", "waffle-jna")
    }
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
        mergeServiceFiles()

        relocateDependency("org.intellij")
        relocateDependency("space.arim.dazzleconf")
        relocateDependency("net.kyori")
        relocateDependency("org.mariadb.jdbc")

        dependencies {
            exclude("META-INF/NOTICE")
            exclude("META-INF/maven/**")
            exclude("META-INF/versions/**")
            exclude("META-INF/**.kotlin_module")
        }

        minimize {
            exclude(dependency("org.jetbrains.exposed:exposed-jdbc:$exposedVersion"))
            exclude(dependency("org.mariadb.jdbc:mariadb-java-client:3.3.0"))
        }
    }
}

bukkit {
    main = "${project.group}.${project.name}"
    authors = listOf("SnapiX")
    depend = listOf("Vault", "LastLoginAPI", "LuckPerms")
}