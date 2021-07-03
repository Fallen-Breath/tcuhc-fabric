package me.fallenbreath.tcuhc.mixins.entity;

import me.fallenbreath.tcuhc.options.Options;
import net.minecraft.entity.EntityCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityCategory.class)
public abstract class EntityCategoryMixin
{
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "getSpawnCap", at = @At("HEAD"), cancellable = true)
	private void modifyMobCap(CallbackInfoReturnable<Integer> cir)
	{
		if (((Object)this) == EntityCategory.MONSTER)
		{
			cir.setReturnValue(Options.instance.getIntegerOptionValue("mobCount"));
		}
	}
}
