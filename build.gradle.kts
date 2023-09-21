import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    application
}

group = "hu.kszi2"
version = "v1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javacord:javacord:3.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("io.ktor:ktor-client-core:2.2.4")
    implementation("io.ktor:ktor-client-cio:2.2.4")

    implementation ("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation ("org.jetbrains.exposed:exposed-crypt:0.43.0")
    implementation ("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation ("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation ("org.jetbrains.exposed:exposed-kotlin-datetime:0.43.0")
    implementation ("org.jetbrains.exposed:exposed-json:0.43.0")
    implementation ("org.jetbrains.exposed:exposed-money:0.43.0")
    implementation ("org.jetbrains.exposed:exposed-spring-boot-starter:0.43.0")
    implementation ("org.xerial:sqlite-jdbc:3.30.1")
}

kotlin {
    jvmToolchain(11)
}

configurations {
    implementation {
        exclude("org.slf4j", "slf4j-simple")
    }
}

application {
    mainClass.set("hu.kszi2.mse.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}
