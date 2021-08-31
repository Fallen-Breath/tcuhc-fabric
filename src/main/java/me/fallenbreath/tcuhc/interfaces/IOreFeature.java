package me.fallenbreath.tcuhc.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public interface IOreFeature
{
	default boolean isValidPositionForValuableOre(IWorld world, BlockPos pos, BlockState oreState)
	{
		return true;
	}
}
