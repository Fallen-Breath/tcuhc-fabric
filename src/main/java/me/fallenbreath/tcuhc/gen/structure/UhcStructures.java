package me.fallenbreath.tcuhc.gen.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import me.fallenbreath.tcuhc.mixins.feature.structure.StructuresConfigAccessor;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;
import java.util.Set;

import static net.minecraft.world.biome.Biome.Category.*;

public class UhcStructures
{
	public static final Set<Biome.Category> CONTINENT_BIOMES = ImmutableSet.of(TAIGA, EXTREME_HILLS, JUNGLE, MESA, PLAINS, SAVANNA, ICY, FOREST, DESERT, SWAMP, MUSHROOM, MOUNTAIN);

	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ?> ENDER_PYRAMID = createDefault("ender_pyramid", new EnderPyramidStructure(DefaultFeatureConfig.CODEC), EnderPyramidStructure.STRUCTURE_CONFIG);

	private static final StructureFeature<GreenhouseConfig> GREENHOUSE = registerStructure("greenhouse", new GreenhouseStructure(GreenhouseConfig.CODEC), GreenhouseStructure.STRUCTURE_CONFIG);
	public static final ConfiguredStructureFeature<GreenhouseConfig, ?> GREENHOUSE_SNOW = UhcRegistry.registerConfiguredStructure("greenhouse_snow", GREENHOUSE.configure(new GreenhouseConfig("snow")));
	public static final ConfiguredStructureFeature<GreenhouseConfig, ?> GREENHOUSE_DESERT = UhcRegistry.registerConfiguredStructure("greenhouse_desert", GREENHOUSE.configure(new GreenhouseConfig("desert")));

	public static void load()
	{
		// no op
	}

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> F registerStructure(String name, F structure, StructureConfig config)
	{
		F struct = UhcRegistry.registerStructure(name, structure, GenerationStep.Feature.SURFACE_STRUCTURES);
		Map<StructureFeature<?>, StructureConfig> map = Maps.newHashMap(StructuresConfigAccessor.getDEFAULT_STRUCTURES());
		map.put(struct, config);
		StructuresConfigAccessor.setDEFAULT_STRUCTURES(new ImmutableMap.Builder<StructureFeature<?>, StructureConfig>().putAll(map).build());
		return struct;
	}

	private static <F extends StructureFeature<DefaultFeatureConfig>> ConfiguredStructureFeature<DefaultFeatureConfig, ?> createDefault(String name, F structure, StructureConfig config)
	{
		return UhcRegistry.registerConfiguredStructure(name, registerStructure(name, structure, config).configure(DefaultFeatureConfig.INSTANCE));
	}
}
