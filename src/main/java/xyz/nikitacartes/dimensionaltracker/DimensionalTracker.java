package xyz.nikitacartes.dimensionaltracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class DimensionalTracker implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(this::copyFrom);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::copyFrom);
    }

    private void copyFrom(ServerPlayerEntity oldPLayer, ServerPlayerEntity newPlayer, boolean alive) {
        PlayerListS2CPacket playerPacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, newPlayer);
        MinecraftServer server = newPlayer.getServer();
        if (server != null) {
            server.getPlayerManager().sendToAll(playerPacket);
        }
    }

    private void copyFrom(ServerPlayerEntity player, ServerWorld oldWorld, ServerWorld newWorld) {
        PlayerListS2CPacket playerPacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player);
        MinecraftServer server = player.getServer();
        if (server != null) {
            server.getPlayerManager().sendToAll(playerPacket);
        }
    }

    public static Formatting getFormatting(LivingEntity entity) {
        World world = entity.getWorld();
        if (world == null) {
            return null;
        }
        return switch (world.getRegistryKey().getValue().toString()) {
            case "minecraft:overworld" -> Formatting.DARK_GREEN;
            case "minecraft:the_nether" -> Formatting.DARK_RED;
            case "minecraft:the_end" -> Formatting.DARK_PURPLE;
            default -> null;
        };
    }

}
