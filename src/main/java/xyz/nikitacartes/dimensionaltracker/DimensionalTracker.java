package xyz.nikitacartes.dimensionaltracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.LinkedHashSet;

public class DimensionalTracker implements ModInitializer {

    private Team overworldTeam;
    private Team netherTeam;
    private Team endTeam;
    public static LinkedHashSet<String> playerCache = new LinkedHashSet<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);

        ServerPlayConnectionEvents.JOIN.register((netHandler, packetSender, server) -> playerCache.add(netHandler.getPlayer().getEntityName()));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> playerCache.add(newPlayer.getEntityName()));
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, fromWorld, toWorld) -> playerCache.add(player.getEntityName()));
    }

    private void onServerTick(MinecraftServer server) {
        if (playerCache.isEmpty()) {
            return;
        }
        LinkedHashSet<String> temp = new LinkedHashSet<>(playerCache);
        playerCache.clear();
        temp.forEach(playerName -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
            if (player != null) {
                if (server.getScoreboard().getTeam(player.getEntityName()) == null) {
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
        });
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        Scoreboard scoreboard = minecraftServer.getScoreboard();
        overworldTeam = scoreboard.getTeam("dimTracker.overworld");
        if (overworldTeam == null) {
            overworldTeam = scoreboard.addTeam("dimTracker.overworld");
            overworldTeam.setColor(Formatting.DARK_GREEN);
        }
        netherTeam = scoreboard.getTeam("dimTracker.nether");
        if (netherTeam == null) {
            netherTeam = scoreboard.addTeam("dimTracker.nether");
            netherTeam.setColor(Formatting.DARK_RED);
        }
        endTeam = scoreboard.getTeam("dimTracker.end");
        if (endTeam == null) {
            endTeam = scoreboard.addTeam("dimTracker.end");
            endTeam.setColor(Formatting.DARK_PURPLE);
        }
    }

    private void onServerStopped(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();
        scoreboard.removeTeam(overworldTeam);
        scoreboard.removeTeam(netherTeam);
        scoreboard.removeTeam(endTeam);
    }

}
