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


java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

dependencies {
    implementation(project(":queste"))
    implementation(project(":api"))
}

allprojects {
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "java")

    java.sourceCompatibility = JavaVersion.VERSION_16
    java.targetCompatibility = JavaVersion.VERSION_16

    val ver by extra("1.0.0")
    var versuffix by extra("-SNAPSHOT")
    val versionsuffix: String? by project
    if (versionsuffix != null) {
        versuffix = "-$versionsuffix"
    }
    project.version = ver + versuffix

    repositories {
        mavenCentral()
        mavenLocal()

        maven {
            url = uri("https://repo.citizensnpcs.co/")
        }

        maven {
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        }

        maven {
            url = uri("https://papermc.io/repo/repository/maven-public/")
        }

        maven {
            url = uri("https://oss.sonatype.org/content/groups/public/")
        }

        // PAPI
        maven {
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi")
        }

        maven {
            url = uri("https://repo.codemc.io/repository/maven-snapshots/")
        }

        maven {
            url = uri("https://repo.codemc.io/repository/nms/")
        }

        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://jitpack.io")
        maven("https://erethon.de/repo")
    }

    //tasks.javadoc.options.encoding = "UTF-8"

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
        testImplementation("com.github.seeseemelk:MockBukkit-v1.17:1.10.0")
        testImplementation("org.reflections:reflections:0.9.12")

        implementation("io.papermc:paperlib:1.0.6") // paperlib - async teleport on Paper
        implementation("com.github.stefvanschie.inventoryframework:IF:0.10.3") // inventory framework
        implementation("com.gitlab.samb440.languagy:api:3ba744c0f1") {
            isTransitive = false
        } // languagy

        compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
        compileOnly("org.jetbrains:annotations:20.1.0")
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

    tasks.withType(Test::class.java) {
        useJUnitPlatform()

        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    tasks.processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}

tasks.shadowJar {
    dependsOn(project(":queste").tasks.build)
    relocate("net.islandearth.languagy", "com.convallyria.queste.libs.languagy")
    relocate("co.aikar.commands", "com.convallyria.queste.libs.acf")
    relocate("co.aikar.locales", "com.convallyria.queste.libs.acf.locales")
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
