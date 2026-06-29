plugins {
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
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
