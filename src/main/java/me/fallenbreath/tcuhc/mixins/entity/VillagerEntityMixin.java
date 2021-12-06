package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity
{
	public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Inject(method = "prepareOffersFor", at = @At("HEAD"), cancellable = true)
	private void dontPrepareIfYouAreAUhcMerchant(CallbackInfo ci)
	{
		if (this.isInvulnerable())
		{
			ci.cancel();
		}
	}
}
