package me.fallenbreath.tcuhc.gen;

import net.minecraft.world.biome.layer.type.InitLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum PureContinentLayer implements InitLayer
{
	INSTANCE;

	@Override
	public int sample(LayerRandomnessSource context, int x, int y)
	{
		return 1;
	}
}