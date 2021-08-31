package me.fallenbreath.tcuhc.mixins.block;

import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(OreBlock.class)
public abstract class OreBlockMixin
{
	@SuppressWarnings("ConstantConditions")
	@ModifyVariable(
			method = "onStacksDropped",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/util/math/intprovider/UniformIntProvider;get(Ljava/util/Random;)I"
			)
	)
	private int noXpForQuartzOre(int exp)
	{
		if ((Object)this == Blocks.NETHER_QUARTZ_ORE)
		{
			return 0;
		}
		return exp;
	}
}
