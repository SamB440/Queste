repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.citizensnpcs.co/")

    maven {
        name = "spigotmc-repo"
        uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        name = "papermc-repo"
        uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        name = "sonatype"
        uri("https://oss.sonatype.org/content/groups/public/")
    }

    // PAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi")

    maven {
        name = "codemc-snapshots"
        uri("https://repo.codemc.io/repository/maven-snapshots/")
    }

    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
    maven("https://erethon.de/repo")
}

dependencies {
    implementation("com.gitlab.samb440.languagy:api:3ba744c0f1") // languagy
    implementation("com.github.stefvanschie.inventoryframework:IF:0.9.1") // inventory framework
    implementation("io.papermc:paperlib:1.0.4") // paperlib - async teleport on Paper

    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("me.clip:placeholderapi:2.10.4") // PAPI
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") // vault
    compileOnly("net.citizensnpcs:citizens-main:2.0.27-SNAPSHOT")
    compileOnly("de.erethon.dungeonsxl:dungeonsxl-api:0.18-SNAPSHOT")
    compileOnly("com.gitlab.SamB440:RPGRegions-2:011fa42402") // 1.3.5
}