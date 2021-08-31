package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity
{
	protected MobEntityMixin(EntityType<? extends LivingEntity> type, World world)
	{
		super(type, world);
	}

	@Redirect(
			method = "checkDespawn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/mob/MobEntity;canImmediatelyDespawn(D)Z",
					ordinal = 0
			)
	)
	private boolean modifyImmediateDespawnChance(MobEntity mobEntity, double distanceSquared)
	{
		return mobEntity.canImmediatelyDespawn(distanceSquared) && this.random.nextInt(200) == 0;
	}

	@ModifyConstant(method = "checkDespawn", constant = @Constant(intValue = 600), allow = 1)
	private int modifyRandomlyDespawnThreshold(int value)
	{
		return 300;
	}

	@ModifyConstant(method = "checkDespawn", constant = @Constant(intValue = 800), allow = 1)
	private int modifyRandomlyDespawnChance(int value)
	{
		return 200;
	}

	// no need in 1.15.2
//	@Redirect(
//			method = "tickNewAi",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/entity/mob/MobEntity;checkDespawn()V"
//			)
//	)
//	private void modifyImmediateDespawnChance(MobEntity mobEntity)
//	{
//		// don't check despawn here
//		// do nothing
//	}
}
