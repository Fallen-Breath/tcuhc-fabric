package me.fallenbreath.tcuhc.mixins.entity;

import me.fallenbreath.tcuhc.options.Options;
import net.minecraft.entity.SpawnGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnGroup.class)
public abstract class EntityCategoryMixin
{
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "getCapacity", at = @At("HEAD"), cancellable = true)
	private void modifyMobCap(CallbackInfoReturnable<Integer> cir)
	{
		if (((Object)this) == SpawnGroup.MONSTER)
		{
			cir.setReturnValue(Options.instance.getIntegerOptionValue("mobCount"));
		}
	}
}
