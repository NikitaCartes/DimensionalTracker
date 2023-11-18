package xyz.nikitacartes.dimensionaltracker.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.network.message.MessageType.params;
import static xyz.nikitacartes.dimensionaltracker.DimensionalTracker.getFormatting;

@Mixin(MessageType.class)
public abstract class MessageTypeMixin {

    @Inject(method = "params(Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/entity/Entity;)Lnet/minecraft/network/message/MessageType$Parameters;", at = @At("HEAD"), cancellable = true)
    private static void decoratePlayerNameInChat(RegistryKey<MessageType> typeKey, Entity entity,
                                                 CallbackInfoReturnable<MessageType.Parameters> cir) {
        if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
            MutableText playerName = Text.of(serverPlayerEntity.getGameProfile().getName()).copy();

            Formatting formatting = getFormatting((LivingEntity) entity);
            if (formatting != null) {
                cir.setReturnValue(params(typeKey, entity.getWorld().getRegistryManager(), playerName.formatted(formatting)));
            }
        }
    }
}
