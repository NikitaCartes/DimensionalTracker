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
