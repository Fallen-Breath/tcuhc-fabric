package me.fallenbreath.tcuhc.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.FeatureConfig;

public class GreenhouseConfig implements FeatureConfig
{
	public static final Codec<GreenhouseConfig> CODEC = Codec.STRING.fieldOf("type").orElse("snow").
			xmap(GreenhouseConfig::new, greenhouseConfig -> greenhouseConfig.type).codec();

	public final String type;

	public GreenhouseConfig(String type)
	{
		this.type = type;
	}
}
