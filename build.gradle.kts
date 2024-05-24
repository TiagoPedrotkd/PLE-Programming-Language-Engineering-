import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("script-runtime"))
    implementation("org.antlr:antlr4:4.13.1")
    implementation("com.github.javaparser:javaparser-symbol-solver-core:3.25.10")
}

tasks.test {
    useJUnitPlatform()
}

// Define a mesma versão da JVM para a compilação Java e Kotlin
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

// Configura o main class para a aplicação, se necessário
application {
    mainClass.set("org.example.MainKt")
}

sourceSets {
    main {
        resources {
            srcDirs("files")
        }
    }
}