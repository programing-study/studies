plugins {
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.springboot)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.spring)
}

group = "com.study"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.h2.database)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.reactor)
    implementation(libs.spring.boot.data.jpa)
    implementation(libs.spring.boot.web)
    implementation(libs.kotlin.logging.jvm)
    implementation(libs.kotlin.reflect)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    // For example:
    jvmToolchain(21)
}

tasks {
    jar {
        enabled = true
    }

    bootJar {
        enabled = false
    }
}
