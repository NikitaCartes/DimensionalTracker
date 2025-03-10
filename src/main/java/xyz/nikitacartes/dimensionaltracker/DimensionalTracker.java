package xyz.nikitacartes.dimensionaltracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Properties;

public class DimensionalTracker implements ModInitializer {
    public static LinkedHashSet<String> playerCache = new LinkedHashSet<>();

    public static boolean enableTeams = true;
    public static boolean enablePlaceholders = false;

    @Override
    public void onInitialize() {
        Path gameDirectory = FabricLoader.getInstance().getGameDir();
        Properties properties = new Properties();
        try {
            // check that the file exists and copy it from the resources if it doesn't
            File file = new File(gameDirectory + "/config/DimensionalTracker.properties");
            if (!file.exists()) {
                InputStream in = getClass().getResourceAsStream("/DimensionalTracker.properties");
                Files.copy(in, file.toPath());
            }
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        enableTeams = Boolean.parseBoolean(properties.getProperty("enable-teams", "true"));
        enablePlaceholders = Boolean.parseBoolean(properties.getProperty("enable-placeholders", "true")) && FabricLoader.getInstance().isModLoaded("placeholder-api");

        if (enableTeams) {
            ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
            ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

            ServerPlayConnectionEvents.JOIN.register((netHandler, packetSender, server) -> playerCache.add(netHandler.getPlayer().getNameForScoreboard()));
            ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> playerCache.add(newPlayer.getNameForScoreboard()));
            ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, fromWorld, toWorld) -> playerCache.add(player.getNameForScoreboard()));
        }

        if (enablePlaceholders) {
            TrackerPlaceholders.loadValue(properties);
        }
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
                ServerScoreboard scoreboard = server.getScoreboard();
                Team playerTeam = scoreboard.getScoreHolderTeam(playerName);
                if (playerTeam == null || playerTeam.getName().startsWith("dimTracker")) {
                    Team team = server.getScoreboard().getTeam("dimTracker." + player.getServerWorld().getRegistryKey().getValue().getPath());
                    if (team != null) {
                        scoreboard.addScoreHolderToTeam(playerName, team);
                    } else if (playerTeam != null) {
                        scoreboard.removeScoreHolderFromTeam(playerName, scoreboard.getScoreHolderTeam(playerName));
                    }
                }
            }
        });
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        Scoreboard scoreboard = minecraftServer.getScoreboard();
        if (scoreboard.getTeam("dimTracker.overworld") == null) {
            scoreboard.addTeam("dimTracker.overworld").setColor(Formatting.DARK_GREEN);
        }
        if (scoreboard.getTeam("dimTracker.the_nether") == null) {
            scoreboard.addTeam("dimTracker.the_nether").setColor(Formatting.DARK_RED);
        }
        if (scoreboard.getTeam("dimTracker.the_end") == null) {
            scoreboard.addTeam("dimTracker.the_end").setColor(Formatting.DARK_PURPLE);
        }
    }

}
