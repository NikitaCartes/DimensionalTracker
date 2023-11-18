package xyz.nikitacartes.dimensionaltracker.mixin;

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

import static xyz.nikitacartes.dimensionaltracker.DimensionalTracker.joinTeam;

@Mixin(value = ServerScoreboard.class)
public class ServerScoreboardMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/Team;)V", at = @At("RETURN"))
    private void removePlayerFromTeam(String playerName, Team team, CallbackInfo ci) {
        ServerPlayerEntity player = this.server.getPlayerManager().getPlayer(playerName);
        if (player != null) {
            joinTeam(player, this.server);
        }
    }
}
