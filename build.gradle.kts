plugins {
    kotlin("jvm") version "1.7.10"
}

group = "me.dolphin2410"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly("io.github.monun:kommand-api:3.0.0")
}

tasks {
    jar {
        archiveFileName.set("boatice.jar")
    }
}