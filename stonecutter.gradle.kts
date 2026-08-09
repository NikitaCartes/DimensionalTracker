plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}
stonecutter active "26.2-fabric"

stonecutter parameters {
    // Loader flag constants: `//? if fabric { ... }` / `//? if neoforge { ... }`.
    constants.match(current.project.substringAfterLast('-'), "fabric", "neoforge")

    // PlayerTeam.setColor(ChatFormatting) became setColor(Optional<TeamColor>) in 26.2.
    // Enabled per-file with a `//~ team_color` header (currently only DimensionalTracker.java).
    replacements.string(current.parsed >= "26.2", "team_color") {
        replace("import net.minecraft.ChatFormatting;", "import java.util.Optional;\nimport net.minecraft.world.scores.TeamColor;")
        replace(".setColor(ChatFormatting.DARK_GREEN)", ".setColor(Optional.of(TeamColor.DARK_GREEN))")
        replace(".setColor(ChatFormatting.DARK_RED)", ".setColor(Optional.of(TeamColor.DARK_RED))")
        replace(".setColor(ChatFormatting.DARK_PURPLE)", ".setColor(Optional.of(TeamColor.DARK_PURPLE))")
    }

    // ResourceLocation became Identifier, and ResourceKey.location() became identifier(), in 1.21.11.
    // Enabled per-file with a `//~ resource_location` header.
    replacements.string(current.parsed >= "1.21.11", "resource_location") {
        replace("ResourceLocation", "Identifier")
        replace(".location()", ".identifier()")
    }

    // placeholder-api 2.x registers with Placeholders.register; 3.x (26.1+) renamed it to
    // registerCommon. The lambda shape is the same in both.
    replacements.string(current.parsed >= "26.1", "placeholder_register") {
        replace("Placeholders.register(", "Placeholders.registerCommon(")
    }
}

stonecutter.tasks {
    // Sort published artifacts by version when running the aggregated publishMods.
    order("publishMods")
}

// One GitHub release for the whole version matrix: this root task creates it (empty),
// and every node's publishGithub uploads its jar into it via `parent`.
publishMods {
    val githubToken = System.getenv("GITHUB_TOKEN") ?: ""
    val modVersion = findProperty("mod_version")?.toString()
        ?: file("stonecutter.properties.toml").readLines()
            .first { it.trim().startsWith("mod_version") }
            .substringAfter('=').trim().trim('"')

    dryRun = githubToken.isEmpty()
    version = modVersion
    displayName = modVersion
    changelog = rootProject.file("RELEASE_NOTE.md").readText()
    type = STABLE

    github {
        accessToken = githubToken
        repository = "NikitaCartes/DimensionalTracker"
        commitish = "master"
        tagName = modVersion
        allowEmptyFiles = true
    }
}
