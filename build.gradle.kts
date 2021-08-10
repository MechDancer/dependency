plugins {
    kotlin("jvm") version "1.5.21"
    `maven-publish`
}

group = "org.mechdancer"
version = "0.1.0-rc-5"

repositories { mavenCentral() }
dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:+")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:+")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:+")
}

java { withSourcesJar(); withJavadocJar() }
tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
tasks.test { useJUnitPlatform() }

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/mechdancer/dependency")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
