plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}

stonecutter {
    val (version, loader) = current.project.split('-', limit = 2)
    properties.tags(version, loader)
    // eu.pb4 placeholder-api has no build for every Minecraft version; nodes that omit
    // placeholder_version compile without the integration.
    constants["placeholders"] = findProperty("placeholder_version") != null
}

repositories {
    mavenCentral()
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
}

val javaVersion = property("java_version").toString().toInt()

base.archivesName = "${property("mod_id")}-fabric-mc${property("minecraft_version")}"
version = property("mod_version").toString()

java {
    withSourcesJar()
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
}

dependencies {
    // Fabric (26.1+ ships Mojang-mapped, so no `mappings(...)` line is needed).
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

    findProperty("placeholder_version")?.let {
        implementation("eu.pb4:placeholder-api:$it")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

tasks.jar {
    from("LICENSE")
}

val modExpansions = mapOf(
    "version" to project.version.toString(),
    "supported_minecraft_version" to property("supported_minecraft_version").toString(),
    "java_version" to javaVersion.toString()
)

tasks.processResources {
    inputs.properties(modExpansions)
    filesMatching("fabric.mod.json") { expand(modExpansions) }
}

tasks.register<Copy>("collectJars") {
    group = "build"
    from(tasks.jar.map { it.archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs"))
    dependsOn("build", rootProject.tasks.named("cleanCollectedJars"))
}

publishMods {
    val modrinthToken = System.getenv("MODRINTH_TOKEN") ?: ""
    val curseforgeToken = System.getenv("CURSEFORGE_TOKEN") ?: ""
    val githubToken = System.getenv("GITHUB_TOKEN") ?: ""

    file = tasks.jar.get().archiveFile
    dryRun = modrinthToken.isEmpty() || curseforgeToken.isEmpty() || githubToken.isEmpty()
    displayName = "${property("display_name")} ${project.version}"
    version = project.version.toString()
    changelog = rootProject.file("RELEASE_NOTE.md").readText()
    type = STABLE
    modLoaders.add("fabric")

    val targets = property("supported_versions").toString().split(",")
    modrinth {
        projectId = "24hKQjf7"
        accessToken = modrinthToken
        targets.forEach(minecraftVersions::add)
        requires("fabric-api")
        optional("placeholder-api")
    }
    curseforge {
        projectId = "940062"
        accessToken = curseforgeToken
        targets.forEach(minecraftVersions::add)
        requires("fabric-api")
        optional("text-placeholder-api")
        server.set(true)
    }
    // Uploads this node's jar into the single release created by the root publishGithub task.
    github {
        accessToken = githubToken
        parent(rootProject.tasks.named("publishGithub"))
    }
}
