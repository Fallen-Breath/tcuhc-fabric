package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import me.fallenbreath.tcuhc.interfaces.IOreFeature;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.OreFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin implements IOreFeature
{
	@Redirect(
			method = "generateVeinPart",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/IWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
			)
	)
	private boolean setBlockStateIfPosValid(IWorld iWorld, BlockPos pos, BlockState state, int flags)
	{
		if (isValidPositionForValuableOre(iWorld, pos))
		{
			return iWorld.setBlockState(pos, state, flags);
		}
		return false;
	}
}
