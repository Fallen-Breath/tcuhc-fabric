package me.fallenbreath.tcuhc.gen;

import com.mojang.datafixers.Dynamic;
import me.fallenbreath.tcuhc.interfaces.IOreFeature;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.function.Function;

public class ValuableOreFeature extends OreFeature implements IOreFeature
{
	public ValuableOreFeature(Function<Dynamic<?>, ? extends OreFeatureConfig> configFactory)
	{
		super(configFactory);
	}

	@Override
	public boolean isValidPositionForValuableOre(IWorld world, BlockPos pos, BlockState oreState)
	{
		if (!UhcFeatures.isValuableOreBlock(oreState.getBlock()))
		{
			return IOreFeature.super.isValidPositionForValuableOre(world, pos, oreState);
		}

		for (int x = -1; x <= 1; x++)
		{
			for (int y = -1; y <= 1; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					if (world.isAir(pos.add(x, y, z)))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
