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

	// StructureFeature
	private static class SF
	{
		private static final StructureFeature<DefaultFeatureConfig> ENDER_PYRAMID = registerStructure("ender_pyramid", new EnderPyramidStructure(DefaultFeatureConfig.CODEC));
		private static final StructureFeature<GreenhouseConfig> GREENHOUSE = registerStructure("greenhouse", new GreenhouseStructure(GreenhouseConfig.CODEC));
		private static final StructureFeature<DefaultFeatureConfig> VILLAIN_HOUSE = registerStructure("villain_house", new VillainHouseStructure(DefaultFeatureConfig.CODEC));
		private static final StructureFeature<DefaultFeatureConfig> HONEY_WORKSHOP = registerStructure("honey_workshop", new HoneyWorkshopStructure(DefaultFeatureConfig.CODEC));

		private static final Map<StructureFeature<?>, StructureConfig> STRUCTURE_CONFIG_MAP = new ImmutableMap.Builder<StructureFeature<?>, StructureConfig>().
				put(ENDER_PYRAMID, new StructureConfig(40, 24, 591497057)).
				put(GREENHOUSE, new StructureConfig(32, 8, 981666224)).
				put(VILLAIN_HOUSE, new StructureConfig(28, 12, 1323770494)).
				put(HONEY_WORKSHOP, new StructureConfig(32, 8, 1124961827)).
				build();

		public static void load()
		{
			Map<StructureFeature<?>, StructureConfig> map = Maps.newHashMap(StructuresConfigAccessor.getDEFAULT_STRUCTURES());
			map.putAll(STRUCTURE_CONFIG_MAP);
			StructuresConfigAccessor.setDEFAULT_STRUCTURES(new ImmutableMap.Builder<StructureFeature<?>, StructureConfig>().putAll(map).build());
		}
	}

	// ConfiguredStructureFeature
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ?> ENDER_PYRAMID = UhcRegistry.registerConfiguredStructure("ender_pyramid", SF.ENDER_PYRAMID.configure(DefaultFeatureConfig.INSTANCE));
	public static final ConfiguredStructureFeature<GreenhouseConfig, ?> GREENHOUSE_SNOW = UhcRegistry.registerConfiguredStructure("greenhouse_snow", SF.GREENHOUSE.configure(new GreenhouseConfig("snow")));
	public static final ConfiguredStructureFeature<GreenhouseConfig, ?> GREENHOUSE_DESERT = UhcRegistry.registerConfiguredStructure("greenhouse_desert", SF.GREENHOUSE.configure(new GreenhouseConfig("desert")));
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ?> VILLAIN_HOUSE = UhcRegistry.registerConfiguredStructure("villain_house", SF.VILLAIN_HOUSE.configure(DefaultFeatureConfig.INSTANCE));
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ?> HONEY_WORKSHOP = UhcRegistry.registerConfiguredStructure("honey_workshop", SF.HONEY_WORKSHOP.configure(DefaultFeatureConfig.INSTANCE));

	public static void load()
	{
		SF.load();
	}

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> F registerStructure(String name, F structure)
	{
		return UhcRegistry.registerStructure(name, structure, GenerationStep.Feature.SURFACE_STRUCTURES);
	}
}
