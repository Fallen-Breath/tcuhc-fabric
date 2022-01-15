package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityDamageSource.class)
public abstract class EntityDamageSourceMixin
{
	@Shadow @Final protected Entity source;

	@Inject(method = "isScaledWithDifficulty", at = @At("HEAD"), cancellable = true)
	private void endCrystalIsAlsoAbleToHaveDamageScaled(CallbackInfoReturnable<Boolean> cir)
	{
		if (this.source instanceof EndCrystalEntity)
		{
			cir.setReturnValue(true);
		}
	}
}
