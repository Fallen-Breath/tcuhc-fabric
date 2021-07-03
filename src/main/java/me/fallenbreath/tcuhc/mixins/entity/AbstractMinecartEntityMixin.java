package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin
{
	@Redirect(
			method = "tick",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity$Type;RIDEABLE:Lnet/minecraft/entity/vehicle/AbstractMinecartEntity$Type;"
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;hasVehicle()Z",
					ordinal = 0
			)
	)
	private boolean hasVehicleAndNotUhcVillager(Entity entity)
	{
		return entity.hasVehicle() && !(entity instanceof VillagerEntity);
	}
}
