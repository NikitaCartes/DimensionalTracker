package xyz.nikitacartes.dimensionaltracker.mixin;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerScoreboard.class)
public class ServerScoreboardMixin {

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "addPlayerToTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/Team;)Z", at = @At("RETURN"))
    private void addPlayerToTeam(String playerName, Team team, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player != null) {
            PlayerListS2CPacket playerPacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player);
            this.server.getPlayerManager().sendToAll(playerPacket);
        }
    }

    @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/Team;)V", at = @At("RETURN"))
    private void removePlayerFromTeam(String playerName, Team team, CallbackInfo ci) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player != null) {
            PlayerListS2CPacket playerPacket = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player);
            this.server.getPlayerManager().sendToAll(playerPacket);
        }
    }
}
