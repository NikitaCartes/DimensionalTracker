package xyz.nikitacartes.dimensionaltracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.ChatFormatting;

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

            ServerPlayConnectionEvents.JOIN.register((netHandler, packetSender, server) -> playerCache.add(netHandler.getPlayer().getScoreboardName()));
            ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> playerCache.add(newPlayer.getScoreboardName()));
            ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, fromWorld, toWorld) -> playerCache.add(player.getScoreboardName()));
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
            ServerPlayer player = server.getPlayerList().getPlayer(playerName);
            if (player != null) {
                ServerScoreboard scoreboard = server.getScoreboard();
                PlayerTeam playerTeam = scoreboard.getPlayerTeam(playerName);
                if (playerTeam == null || playerTeam.getName().startsWith("dimTracker")) {
                    PlayerTeam team = server.getScoreboard().getPlayerTeam("dimTracker." + player.level().dimension().identifier().getPath());
                    if (team != null) {
                        scoreboard.addPlayerToTeam(playerName, team);
                    } else if (playerTeam != null) {
                        scoreboard.removePlayerFromTeam(playerName, scoreboard.getPlayerTeam(playerName));
                    }
                }
            }
        });
    }

    private void onServerStarted(MinecraftServer minecraftServer) {
        Scoreboard scoreboard = minecraftServer.getScoreboard();
        if (scoreboard.getPlayersTeam("dimTracker.overworld") == null) {
            scoreboard.addPlayerTeam("dimTracker.overworld").setColor(ChatFormatting.DARK_GREEN);
        }
        if (scoreboard.getPlayersTeam("dimTracker.the_nether") == null) {
            scoreboard.addPlayerTeam("dimTracker.the_nether").setColor(ChatFormatting.DARK_RED);
        }
        if (scoreboard.getPlayersTeam("dimTracker.the_end") == null) {
            scoreboard.addPlayerTeam("dimTracker.the_end").setColor(ChatFormatting.DARK_PURPLE);
        }
    }

}
