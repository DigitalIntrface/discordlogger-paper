plugins {
    id("java")
}

group = "com.DiscordLogger"
version = "1.43.8"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.14.0")
    compileOnly("net.kyori:adventure-api:4.14.0")
    compileOnly("com.google.code.gson:gson:2.13.1")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.test {
    useJUnitPlatform()
}