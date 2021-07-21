buildscript {
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("java")
}

val group = "com.convallyria.queste"
var version by extra("1.0.0")

java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

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
    implementation(project(":queste"))
    implementation(project(":api"))
}

allprojects {
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "java")

    java.sourceCompatibility = JavaVersion.VERSION_16
    java.targetCompatibility = JavaVersion.VERSION_16

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

    //tasks.javadoc.options.encoding = "UTF-8"

    dependencies {
        testImplementation(group = "junit", name = "junit", version = "4.5")
        testImplementation("com.github.seeseemelk:MockBukkit-v1.16:0.5.0")
        testImplementation("org.reflections:reflections:0.9.12")
    }

    tasks.processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    tasks.build {
        dependsOn(tasks.shadowJar)
        dependsOn(tasks.javadoc)
    }
}

tasks.shadowJar {
    dependsOn(project(":queste"))
    relocate("net.islandearth.languagy", "com.convallyria.queste.libs.languagy")
    relocate("co.aikar.commands", "com.convallyria.queste.libs.acf")
    relocate("co.aikar.idb", "com.convallyria.queste.libs.idb")
    relocate("com.github.stefvanschie.inventoryframework", "com.convallyria.queste.libs.inventoryframework")
    relocate("org.bstats", "com.convallyria.queste.libs.bstats")
    relocate("io.papermc.lib", "com.convallyria.queste.libs.paperlib")
    relocate("xyz.upperlevel.spigot", "com.convallyria.queste.libs.book")
    relocate("org.apache.commons:commons-lang3", "com.convallyria.queste.libs.apache.commons")
    relocate("me.lucko.helper", "com.convallyria.queste.libs.me.lucko.helper")
}

// Thanks to: PlotSquared
val javadocDir = rootDir.resolve("docs").resolve("javadoc").resolve(project.name)
tasks {
    val aggregatedJavadocs = create<Javadoc>("aggregatedJavadocs") {
        title = "${project.name} ${project.version} API"
        setDestinationDir(javadocDir)
        options.destinationDirectory = javadocDir

        doFirst {
            javadocDir.deleteRecursively()
        }
    }.also {
        it.group = "Documentation"
        it.description = "Generate javadocs from all child projects as if it was a single project"
    }

    subprojects.forEach { subProject ->
        subProject.afterEvaluate {
            subProject.tasks.withType<Javadoc>().forEach { task ->
                aggregatedJavadocs.source += task.source
                aggregatedJavadocs.classpath += task.classpath
                aggregatedJavadocs.excludes += task.excludes
                aggregatedJavadocs.includes += task.includes

                val rootOptions = aggregatedJavadocs.options as StandardJavadocDocletOptions
                val subOptions = task.options as StandardJavadocDocletOptions
                rootOptions.links(*subOptions.links.orEmpty().minus(rootOptions.links.orEmpty()).toTypedArray())
            }
        }
    }

    build {
        dependsOn(aggregatedJavadocs)
    }
}

tasks.build {
    dependsOn(project(":api").tasks.build)
    dependsOn(project(":queste").tasks.build)
    dependsOn(tasks.shadowJar)
    //dependsOn(tasks.aggregatedJavadocs)
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}
