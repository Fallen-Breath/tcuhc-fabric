package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity
{
	protected WitchEntityMixin(EntityType<? extends RaiderEntity> type, World world)
	{
		super(type, world);
	}

	/**
	 * TC Plugin: change witch potion drop chance to 100%
	 */
	@Inject(method = "<init>", at = @At("TAIL"))
	private void constructUhcGameManager(CallbackInfo ci)
	{
		this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0F;
	}
}
