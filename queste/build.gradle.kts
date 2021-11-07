plugins {
    id("com.github.johnrengelman.shadow")
    id("java")
    kotlin("jvm") version "1.5.31"
}

dependencies {
    implementation("co.aikar:acf-paper:0.5.0-SNAPSHOT") // commands
    implementation("co.aikar:idb-core:1.0.0-SNAPSHOT") // database
    // implementation("com.zaxxer:HikariCP:4.0.3") // database // Added to plugin.yml
    implementation("org.bstats:bstats-bukkit:2.2.1") // plugin stats
    implementation("net.wesjd:anvilgui:1.5.1-SNAPSHOT") // anvilgui
    implementation("xyz.upperlevel.spigot.book:spigot-book-api:1.5")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    compileOnly("me.clip:placeholderapi:2.10.4") // PAPI
    compileOnly ("com.github.MilkBowl:VaultAPI:1.7") { // vault
        exclude(group = "org.bukkit")
    }
    compileOnly("net.citizensnpcs:citizens-main:2.0.27-SNAPSHOT")
    compileOnly("de.erethon.dungeonsxl:dungeonsxl-api:0.18-PRE-02")
    compileOnly("com.gitlab.SamB440:RPGRegions-2:011fa42402") // 1.3.5
    implementation(project(":api"))
}

tasks.javadoc {
    exclude("xyz/upperlevel/spigot/book/**")
    exclude("hu/trigary/advancementcreator/**")
    exclude("me/lucko/helper")
}

tasks.build {
    dependsOn(project(":api").tasks.build)
    dependsOn(tasks.javadoc)
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "16"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "16"
    }
}
