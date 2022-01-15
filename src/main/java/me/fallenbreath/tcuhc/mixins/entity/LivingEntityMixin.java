package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Shadow public abstract void setAbsorptionAmount(float amount);

	/**
	 * Clean the absorption amount manually here due to {@link StatusEffectInstanceMixin#alwaysOverwriteForAbsorptionEffect}
	 * To prevent eating egapple then eating gapple then /effect clear resulting in remaining absorption amount
	 */
	@SuppressWarnings("JavadocReference")
	@Inject(method = "clearStatusEffects", at = @At("TAIL"))
	private void removeAbsorptionYellowHearts(CallbackInfoReturnable<Boolean> cir)
	{
		this.setAbsorptionAmount(0.0F);
	}
}
