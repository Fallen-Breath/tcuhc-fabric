package me.fallenbreath.tcuhc.mixins.item;

import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FlowerBlock.class)
public abstract class FlowerBlockMixin
{
	@Shadow @Final private StatusEffect effectInStew;

	@Mutable
	@Shadow @Final private int effectInStewDuration;

	/**
	 * The regeneration of vanilla oxeye daisy is too op, regen 3 hp
	 * We reduce the amount of hp to 1 by reducing the effect duration
	 */
	@Inject(method = "<init>", at = @At("TAIL"))
	private void modifyOxeyeDaisyRegenerationDuration(CallbackInfo ci)
	{
		if (this.effectInStew == StatusEffects.REGENERATION)
		{
			this.effectInStewDuration /= 3;
		}
	}
}
