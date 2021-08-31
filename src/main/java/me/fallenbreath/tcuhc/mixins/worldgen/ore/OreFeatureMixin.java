package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import me.fallenbreath.tcuhc.interfaces.IOreFeature;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
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
					target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
			)
	)
	private boolean setBlockStateIfPosValid(WorldAccess world, BlockPos pos, BlockState state, int flags)
	{
		if (isValidPositionForValuableOre(world, pos, state))
		{
			return world.setBlockState(pos, state, flags);
		}
		return false;
	}
}
