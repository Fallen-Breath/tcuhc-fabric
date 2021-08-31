package me.fallenbreath.tcuhc.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.function.Function;

public interface IOreFeature
{
	default boolean isValidPositionForValuableOre(Function<BlockPos, BlockState> posToState, BlockPos pos, BlockState oreState)
	{
		return true;
	}
}
