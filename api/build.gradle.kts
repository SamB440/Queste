dependencies {
    implementation("com.gitlab.samb440.languagy:api:3ba744c0f1") {
        isTransitive = false
    } // languagy
    implementation("com.github.stefvanschie.inventoryframework:IF:0.9.1") // inventory framework
    implementation("io.papermc:paperlib:1.0.4") // paperlib - async teleport on Paper

    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("me.clip:placeholderapi:2.10.4") // PAPI
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") // vault
    compileOnly("net.citizensnpcs:citizens-main:2.0.27-SNAPSHOT")
    compileOnly("de.erethon.dungeonsxl:dungeonsxl-api:0.18-PRE-02")
    compileOnly("com.gitlab.SamB440:RPGRegions-2:011fa42402") // 1.3.5
}