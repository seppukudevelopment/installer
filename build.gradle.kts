import org.jetbrains.kotlin.daemon.common.OSKind

plugins {
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "pw.seppuku"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven("https://jitpack.io")
}

val lwjglNatives = when (OSKind.current) {
    OSKind.Windows -> {
        "natives-windows"
    }

    OSKind.Unix -> {
        "natives-linux"
    }

    OSKind.OSX -> {
        "natives-macos"
    }

    else -> throw Exception("Unsupported operating system")
}

project.configurations.implementation.get().isCanBeResolved = true
project.configurations.runtimeOnly.get().isCanBeResolved = true

dependencies {
    implementation(kotlin( "stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    implementation("com.twelvemonkeys.imageio:imageio-core:3.6.1")
    implementation("com.twelvemonkeys.imageio:imageio-bmp:3.6.1")

    implementation("org.lwjgl:lwjgl:3.2.3")
    implementation("org.lwjgl:lwjgl-opengl:3.2.3")
    implementation("org.lwjgl:lwjgl-glfw:3.2.3")
    implementation("org.lwjgl:lwjgl-stb:3.2.3")

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    listOf("gl", "glfw", "core").forEach {
        implementation("com.github.kotlin-graphics.imgui:$it:4e5eec4af9") {
            exclude("org.lwjgl")
        }
    }

    configurations.implementation.get().resolvedConfiguration.resolvedArtifacts.forEach {
        if (it.moduleVersion.id.group == "org.lwjgl") {
            runtimeOnly("org.lwjgl:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}:natives-windows")
            runtimeOnly("org.lwjgl:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}:natives-linux")
            runtimeOnly("org.lwjgl:${it.moduleVersion.id.name}:${it.moduleVersion.id.version}:natives-macos")
        }
    }
}

tasks {
    shadowJar {
        configurations = listOf(project.configurations.implementation.get(), project.configurations.runtimeOnly.get())
        minimize() {
            exclude(dependency("com.twelvemonkeys.imageio:.*:.*"))
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.languageVersion = "1.4"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.languageVersion = "1.4"
    }

    jar {
        manifest {
            attributes["Main-Class"] = "MainKt"
        }
    }
}