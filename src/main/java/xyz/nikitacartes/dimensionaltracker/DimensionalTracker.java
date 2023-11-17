package xyz.nikitacartes.dimensionaltracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class DimensionalTracker implements ModInitializer {

    private Team overworldTeam;
    private Team netherTeam;
    private Team endTeam;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);

        ServerPlayConnectionEvents.JOIN.register((netHandler, packetSender, server) -> joinTeam(netHandler.getPlayer(), server));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> joinTeam(newPlayer, newPlayer.getServer()));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, fromWorld, toWorld) -> joinTeam(player, player.getServer()));
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        Scoreboard scoreboard = minecraftServer.getScoreboard();
        overworldTeam = scoreboard.getTeam("dimTracker_overworld");
        if (overworldTeam == null) {
            overworldTeam = scoreboard.addTeam("dimTracker_overworld");
            overworldTeam.setColor(Formatting.DARK_GREEN);
        }
        netherTeam = scoreboard.getTeam("dimTracker_nether");
        if (netherTeam == null) {
            netherTeam = scoreboard.addTeam("dimTracker_nether");
            netherTeam.setColor(Formatting.DARK_RED);
        }
        endTeam = scoreboard.getTeam("dimTracker_end");
        if (endTeam == null) {
            endTeam = scoreboard.addTeam("dimTracker_end");
            endTeam.setColor(Formatting.DARK_PURPLE);
        }
    }

    private void joinTeam(ServerPlayerEntity player, MinecraftServer server) {
        switch (player.getServerWorld().getRegistryKey().getValue().toString()) {
            case "minecraft:overworld":
                server.getScoreboard().addPlayerToTeam(player.getEntityName(), overworldTeam);
                break;
            case "minecraft:the_nether":
                server.getScoreboard().addPlayerToTeam(player.getEntityName(), netherTeam);
                break;
            case "minecraft:the_end":
                server.getScoreboard().addPlayerToTeam(player.getEntityName(), endTeam);
                break;
        }
    }

}
