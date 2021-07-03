package me.fallenbreath.tcuhc.mixins.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OreBlock.class)
public abstract class OreBlockMixin
{
	@SuppressWarnings("ConstantConditions")
	@Inject(method = "getExperienceWhenMined", at = @At("HEAD"), cancellable = true)
	private void noXpForQuartzOre(CallbackInfoReturnable<Integer> cir)
	{
		if ((Object)this == Blocks.NETHER_QUARTZ_ORE)
		{
			cir.setReturnValue(0);
		}
	}
}
