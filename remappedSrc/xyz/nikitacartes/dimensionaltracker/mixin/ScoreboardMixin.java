package xyz.nikitacartes.dimensionaltracker.mixin;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static xyz.nikitacartes.dimensionaltracker.DimensionalTracker.playerCache;

@Mixin(value = Scoreboard.class)
public class ScoreboardMixin {

    @Inject(method = "removeTeam(Lnet/minecraft/scoreboard/Team;)V", at = @At("HEAD"))
    private void addDimensionTeam(Team team, CallbackInfo ci) {
        playerCache.addAll(team.getPlayerList());
    }

    @Inject(method = "addPlayerToTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/Team;)Z", at = @At("HEAD"))
    private void addDimensionTeam(String playerName, Team team, CallbackInfoReturnable<Boolean> cir) {
        playerCache.remove(playerName);
    }

    @Inject(method = "removePlayerFromTeam(Ljava/lang/String;Lnet/minecraft/scoreboard/Team;)V", at = @At("HEAD"))
    private void addDimensionTeam(String playerName, Team team, CallbackInfo ci) {
        if (!team.getName().startsWith("dimTracker")) {
            playerCache.add(playerName);
        }
    }
}
