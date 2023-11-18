package xyz.nikitacartes.dimensionaltracker.mixin;

import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.Optional;

import static xyz.nikitacartes.dimensionaltracker.DimensionalTracker.*;

@Mixin(value = TeamS2CPacket.class)
public class TeamS2CPacketMixin {

    @Shadow
    @Final
    private static int ADD;

    @Shadow
    @Final
    private static int ADD_PLAYERS;

    @Inject(method = "<init>(Ljava/lang/String;ILjava/util/Optional;Ljava/util/Collection;)V", at = @At("RETURN"))
    private void addDimensionTeam(String teamName, int packetType, Optional team, Collection<String> playerNames, CallbackInfo ci) {
        if (playerNames == null) {
            return;
        }
        if (packetType == ADD || packetType == ADD_PLAYERS) {
            playerCache.removeAll(playerNames);
        } else {
            playerCache.addAll(playerNames);
        }
    }
}
