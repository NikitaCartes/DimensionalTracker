package xyz.nikitacartes.dimensionaltracker.mixin;

import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static xyz.nikitacartes.dimensionaltracker.DimensionalTracker.enableTeams;
import static xyz.nikitacartes.dimensionaltracker.DimensionalTracker.playerCache;

@Mixin(value = Scoreboard.class)
public class ScoreboardMixin {

    @Inject(method = "removePlayerTeam(Lnet/minecraft/world/scores/PlayerTeam;)V", at = @At("HEAD"))
    private void addDimensionTeam(PlayerTeam team, CallbackInfo ci) {
        if (enableTeams) playerCache.addAll(team.getPlayers());
    }

    @Inject(method = "addPlayerToTeam(Ljava/lang/String;Lnet/minecraft/world/scores/PlayerTeam;)Z", at = @At("HEAD"))
    private void addDimensionTeam(String playerName, PlayerTeam team, CallbackInfoReturnable<Boolean> cir) {
        if (enableTeams) playerCache.remove(playerName);
    }

    @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/world/scores/PlayerTeam;)V", at = @At("HEAD"))
    private void addDimensionTeam(String playerName, PlayerTeam team, CallbackInfo ci) {
        if (enableTeams && !team.getName().startsWith("dimTracker")) {
            playerCache.add(playerName);
        }
    }
}
