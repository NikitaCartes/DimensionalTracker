package xyz.nikitacartes.dimensionaltracker.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static xyz.nikitacartes.dimensionaltracker.DimensionalTracker.getFormatting;


@Mixin(value = PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "getDisplayName()Lnet/minecraft/text/Text;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addTellClickEvent(Lnet/minecraft/text/MutableText;)Lnet/minecraft/text/MutableText;"))
    private MutableText postGetTabListDisplayName(MutableText component) {
        if (component.getStyle().getColor() != null) {
            return component;
        }

        Formatting formatting = getFormatting(this);
        if (formatting != null) {
            return component.formatted(formatting);
        }
        return component;
    }
}
