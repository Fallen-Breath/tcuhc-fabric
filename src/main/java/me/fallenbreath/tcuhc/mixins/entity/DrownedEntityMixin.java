package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrownedEntity.class)
public abstract class DrownedEntityMixin extends ZombieEntity
{
	public DrownedEntityMixin(EntityType<? extends ZombieEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Inject(method = "initialize", at = @At("TAIL"))
	private void drownedAlwaysDropsTridentIfHolding(CallbackInfoReturnable<EntityData> cir)
	{
		if (this.getEquippedStack(EquipmentSlot.MAINHAND).getItem() == Items.TRIDENT)
		{
			this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0F;
		}
	}
}
