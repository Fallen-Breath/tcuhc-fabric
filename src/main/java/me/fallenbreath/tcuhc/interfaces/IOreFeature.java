package me.fallenbreath.tcuhc.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface IOreFeature
{
	default boolean isValidPositionForValuableOre(WorldAccess world, BlockPos pos, BlockState oreState)
	{
		return true;
	}
}
