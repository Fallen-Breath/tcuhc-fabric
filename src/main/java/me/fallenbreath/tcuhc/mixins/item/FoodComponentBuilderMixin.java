package me.fallenbreath.tcuhc.mixins.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoodComponent.Builder.class)
public abstract class FoodComponentBuilderMixin
{
	@Inject(method = "statusEffect", at = @At("HEAD"))
	private void modifyAbsorptionLevel(StatusEffectInstance effect, float chance, CallbackInfoReturnable<FoodComponent.Builder> cir)
	{
		if (effect.getEffectType() == StatusEffects.ABSORPTION)
		{
			int amp = (effect.getAmplifier() + 1) * 4 - 1;
			((StatusEffectInstanceAccessor)effect).setAmplifier(amp);
		}
	}
}
