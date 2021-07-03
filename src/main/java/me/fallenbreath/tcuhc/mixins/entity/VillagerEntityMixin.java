package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends AbstractTraderEntity
{
	public VillagerEntityMixin(EntityType<? extends AbstractTraderEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Inject(method = "prepareRecipesFor", at = @At("HEAD"), cancellable = true)
	private void dontPrepareIfYouAreAUhcMerchant(CallbackInfo ci)
	{
		if (this.isInvulnerable())
		{
			ci.cancel();
		}
	}
}
