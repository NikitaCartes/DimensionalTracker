package xyz.nikitacartes.dimensionaltracker.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.World.*;


@Mixin(value = ServerPlayerEntity.class, priority = 1100)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getPlayerListName()Lnet/minecraft/text/Text;", at = @At("RETURN"), cancellable = true)
    private void postGetTabListDisplayName(CallbackInfoReturnable<Text> cir) {

        AbstractTeam team = this.getScoreboardTeam();
        if (team != null) {
            if (team.getColor() != null) {
                return;
            }
        }

        MutableText playerName = Text.of(this.getGameProfile().getName()).copy();

        RegistryKey<World> registryKey = this.getWorld().getRegistryKey();
        if (registryKey.equals(OVERWORLD)) {
            cir.setReturnValue(playerName.formatted(Formatting.DARK_GREEN));
        } else if (registryKey.equals(NETHER)) {
            cir.setReturnValue(playerName.formatted(Formatting.DARK_RED));
        } else if (registryKey.equals(END)) {
            cir.setReturnValue(playerName.formatted(Formatting.DARK_PURPLE));
        }
    }
}
