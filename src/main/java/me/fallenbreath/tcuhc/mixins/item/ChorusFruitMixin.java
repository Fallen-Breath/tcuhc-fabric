package me.fallenbreath.tcuhc.mixins.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ChorusFruitItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChorusFruitItem.class)
public abstract class ChorusFruitMixin
{
	@Redirect(
			method = "finishUsing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/LivingEntity;teleport(DDDZ)Z"
			)
	)
	private boolean onlyTeleportIntoWorldBorder(LivingEntity livingEntity, double x, double y, double z, boolean particleEffects)
	{
		Vec3d vec3d = new Vec3d(x, y, z);
		if (livingEntity.getEntityWorld().getWorldBorder().contains(new Box(vec3d, vec3d)))
		{
			return livingEntity.teleport(x, y, z, particleEffects);
		}
		return false;
	}
}
