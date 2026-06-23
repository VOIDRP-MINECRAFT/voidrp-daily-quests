plugins {
    java
    id("com.gradleup.shadow") version "8.3.10"
}

group = "ru.voidrp"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        isTransitive = false
    }
    implementation("com.google.code.gson:gson:2.11.0")
    compileOnly(files("../voidrp_gamesync_plugin/build/libs/voidrp-game-sync-paper-1.4.0-all.jar"))
    compileOnly(files("../voidrp_mod_sell/build/libs/voidrp-mod-sell-1.0.0-all.jar"))
}

repositories {
    maven("https://jitpack.io")
}

tasks {
    processResources {
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(mapOf("version" to project.version))
        }
    }
    shadowJar {
        archiveClassifier.set("all")
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
        relocate("com.google.gson", "ru.voidrp.dailyquests.libs.gson")
    }
    build { dependsOn(shadowJar) }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}
