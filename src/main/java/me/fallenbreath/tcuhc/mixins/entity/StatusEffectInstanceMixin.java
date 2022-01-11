package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin
{
	@Shadow @Final private StatusEffect type;

	/**
	 *  The result behavior will be using the maximum absorption between current absorption and the new absorption
	 *  It's the result we want instead of the vanilla one, which rejects to apply golden apple's absorption when having
	 *  enchanted golden apple's absorption effect with 0 yellow heart
	 *  At least for randomly eating golden apple and enchanted golden apple
	 *
	 *  See the following methods for more Minecraft related implementation: the new effect will be re-applied so
	 *  the absorption stuffs works perfectly
	 *  - {@link net.minecraft.entity.LivingEntity#addStatusEffect(net.minecraft.entity.effect.StatusEffectInstance, net.minecraft.entity.Entity)}
	 *  - {@link net.minecraft.entity.LivingEntity#onStatusEffectUpgraded(net.minecraft.entity.effect.StatusEffectInstance, boolean, net.minecraft.entity.Entity)}
	 *
	 *  {@link LivingEntityMixin#removeAbsorptionYellowHearts} fixes a side effect of this patch
	 */
	@SuppressWarnings("JavadocReference")
	@Redirect(
			method = "upgrade",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/entity/effect/StatusEffectInstance;amplifier:I",
					ordinal = 0
			)
	)
	private int alwaysOverwriteForAbsorptionEffect(StatusEffectInstance that)
	{
		// if (that.amplifier > this.amplifier)
		//           ^ redirected
		if (this.type == StatusEffects.ABSORPTION)
		{
			// always results in true for the if statement
			// so the new effect will always overwrite the exists effect
			return Integer.MAX_VALUE;
		}

		// vanilla
		return that.getAmplifier();
	}
}
