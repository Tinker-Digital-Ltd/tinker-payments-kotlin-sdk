plugins {
    kotlin("jvm") version "1.9.22"
    `maven-publish`
    `java-library`
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

group = "co.ke.tinker"
version = "0.1.0"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

ktlint {
    version.set("0.50.0")
    debug.set(false)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(true)
    enableExperimentalRules.set(false)
    filter {
        exclude("**/generated/**")
        exclude("**/build/**")
    }
}

tasks.named("check") {
    dependsOn("ktlintCheck",)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "17"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("Tinker Payments Kotlin SDK")
                description.set("Official Kotlin SDK for Tinker Payments API")
                url.set("https://payments.tinker.co.ke")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        name.set("Edgar Alochi")
                        email.set("edgarchris4@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/tinker/payments-kotlin-sdk.git")
                    developerConnection.set("scm:git:ssh://github.com/tinker/payments-kotlin-sdk.git")
                    url.set("https://github.com/tinker/payments-kotlin-sdk")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tinker/payments-kotlin-sdk")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
