import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.2"
    id("com.github.gmazzo.buildconfig") version ("3.1.0")
    id("maven-publish")
}

group "ua.nanit"
version "1.8.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("io.netty:netty-all:4.1.101.Final")
    implementation("net.kyori:adventure-nbt:4.14.0")
    implementation("com.grack:nanojson:1.8")
    implementation("com.google.code.gson:gson:2.10.1")
}

buildConfig {
    className("BuildConfig")
    packageName("ua.nanit.limbo")
    buildConfigField("String", "LIMBO_VERSION", "\"${project.version}\"")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    withType<ShadowJar> {
        from("LICENSE")

        manifest {
            attributes("Main-Class" to "ua.nanit.limbo.NanoLimbo")
        }

        minimize {
            exclude(dependency("ch.qos.logback:logback-classic:.*:.*"))
        }

    }

    test {
        useJUnitPlatform()
    }

}

val isCi = System.getenv("GITHUB_EVENT_NAME") != null

publishing {
    if (isCi) {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/poweredbyapartium/nanolimbo")
                credentials {
                    username = System.getenv("USERNAME")
                    password = System.getenv("TOKEN")
                }
            }
        }
    }
}