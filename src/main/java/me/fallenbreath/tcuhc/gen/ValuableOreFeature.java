package me.fallenbreath.tcuhc.gen;

import com.mojang.serialization.Codec;
import me.fallenbreath.tcuhc.interfaces.IOreFeature;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.function.Function;

public class ValuableOreFeature extends OreFeature implements IOreFeature
{
	public ValuableOreFeature(Codec<OreFeatureConfig> codec)
	{
		super(codec);
	}

	@Override
	public boolean isValidPositionForValuableOre(Function<BlockPos, BlockState> posToState, BlockPos pos, BlockState oreState)
	{
		if (!UhcFeatures.isValuableOreBlock(oreState.getBlock()))
		{
			return IOreFeature.super.isValidPositionForValuableOre(posToState, pos, oreState);
		}

		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					if (posToState.apply(pos.add(x, y, z)).isAir())
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
