package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin
{
	@Shadow public abstract void setCritical(boolean critical);

	/**
	 * When the projectile hits an entity but fails to damage the entity, cancel its critical state
	 */
	@Inject(
			method = "onEntityHit",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;setFireTicks(I)V"
			)
	)
	private void cancelCritical(CallbackInfo ci)
	{
		this.setCritical(false);
	}
}
