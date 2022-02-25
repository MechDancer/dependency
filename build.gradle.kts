plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`
    signing
}

group = "org.mechdancer"
version = "0.1.1"

repositories { mavenCentral() }
dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")

    testImplementation(kotlin("test-junit"))
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])
            pom {
                name.set("Dependency")
                description.set("A lightweight dependency injection library")
                url.set("https://github.com/MechDancer/dependency")
                licenses {
                    license {
                        name.set("WTFPL")
                        url.set("http://www.wtfpl.net/txt/copying/")
                    }
                }
                developers {
                    developer {
                        id.set("berberman")
                        name.set("Potato Hatsue")
                        email.set("berberman@yandex.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/MechDancer/dependency.git")
                    developerConnection.set("scm:git:ssh://github.com/MechDancer/dependency.git")
                    url.set("https://github.com/MechDancer/dependency")
                }

            }
        }
    }
    repositories {
        maven("https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
            name = "Sonatype"
            credentials {
                username = properties.getOrDefault("sonatypeUsername", "")?.toString()
                password = properties.getOrDefault("sonatypePassword", "")?.toString()
            }
        }
    }
    signing {
        sign(publishing.publications["maven"])
    }
    tasks.javadoc {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }
}
