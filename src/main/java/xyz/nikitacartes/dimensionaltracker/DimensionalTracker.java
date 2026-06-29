//~ team_color
package xyz.nikitacartes.dimensionaltracker;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
//?} else {
/*import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
*///?}

import net.minecraft.world.scores.Scoreboard;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import java.util.Optional;
import net.minecraft.world.scores.TeamColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Properties;

//? if fabric {
public class DimensionalTracker implements ModInitializer {
//?} else {
/*@net.neoforged.fml.common.Mod("dimensionaltracker")
public class DimensionalTracker {
*///?}

    public static LinkedHashSet<String> playerCache = new LinkedHashSet<>();

    public static boolean enableTeams = true;
    public static boolean enablePlaceholders = false;

    private static Path gameDir() {
        //? if fabric {
        return FabricLoader.getInstance().getGameDir();
        //?} else
        /*return net.neoforged.fml.loading.FMLPaths.GAMEDIR.get();*/
    }

    public void init() {
        Path gameDirectory = gameDir();
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
        //? if fabric {
        enablePlaceholders = Boolean.parseBoolean(properties.getProperty("enable-placeholders", "true")) && FabricLoader.getInstance().isModLoaded("placeholder-api");
        //?}

        if (enableTeams) {
            registerTeamEvents();
        }

        //? if fabric {
        if (enablePlaceholders) {
            TrackerPlaceholders.loadValue(properties);
        }
        //?}
    }

    private void registerTeamEvents() {
        //? if fabric {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        ServerPlayConnectionEvents.JOIN.register((netHandler, packetSender, server) -> playerCache.add(netHandler.getPlayer().getScoreboardName()));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> playerCache.add(newPlayer.getScoreboardName()));
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, fromWorld, toWorld) -> playerCache.add(player.getScoreboardName()));
        //?} else {
        /*NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> onServerStarted(event.getServer()));
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> onServerTick(event.getServer()));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> playerCache.add(event.getEntity().getScoreboardName()));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerRespawnEvent event) -> playerCache.add(event.getEntity().getScoreboardName()));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerChangedDimensionEvent event) -> playerCache.add(event.getEntity().getScoreboardName()));
        *///?}
    }

    //? if fabric {
    @Override
    public void onInitialize() {
        init();
    }
    //?} else {
    /*public DimensionalTracker() {
        init();
    }
    *///?}

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
            scoreboard.addPlayerTeam("dimTracker.overworld").setColor(Optional.of(TeamColor.DARK_GREEN));
        }
        if (scoreboard.getPlayersTeam("dimTracker.the_nether") == null) {
            scoreboard.addPlayerTeam("dimTracker.the_nether").setColor(Optional.of(TeamColor.DARK_RED));
        }
        if (scoreboard.getPlayersTeam("dimTracker.the_end") == null) {
            scoreboard.addPlayerTeam("dimTracker.the_end").setColor(Optional.of(TeamColor.DARK_PURPLE));
        }
    }

}
