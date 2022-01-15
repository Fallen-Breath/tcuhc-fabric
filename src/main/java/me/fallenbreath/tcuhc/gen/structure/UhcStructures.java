package me.fallenbreath.tcuhc.gen.structure;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import me.fallenbreath.tcuhc.mixins.feature.structure.StructuresConfigAccessor;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

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
		private static final StructureFeature<DefaultFeatureConfig> PLAIN_COTTAGE = registerStructure("plain_cottage", new PlainCottageStructure(DefaultFeatureConfig.CODEC));

		private static final Map<StructureFeature<?>, StructureConfig> STRUCTURE_CONFIG_MAP = new ImmutableMap.Builder<StructureFeature<?>, StructureConfig>().
				put(ENDER_PYRAMID, new StructureConfig(40, 24, 591497057)).
				put(GREENHOUSE, new StructureConfig(32, 8, 981666224)).
				put(VILLAIN_HOUSE, new StructureConfig(28, 12, 1323770494)).
				put(HONEY_WORKSHOP, new StructureConfig(32, 8, 1124961827)).
				put(PLAIN_COTTAGE, new StructureConfig(40, 12, 301313065)).
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
	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ?> PLAIN_COTTAGE = UhcRegistry.registerConfiguredStructure("plain_cottage", SF.PLAIN_COTTAGE.configure(DefaultFeatureConfig.INSTANCE));

	public static void load()
	{
		SF.load();
	}

	private static <FC extends FeatureConfig, F extends StructureFeature<FC>> F registerStructure(String name, F structure)
	{
		return UhcRegistry.registerStructure(name, structure, GenerationStep.Feature.SURFACE_STRUCTURES);
	}

	public static void bindUhcStructureToBiomes(
			BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar,
			Set<RegistryKey<Biome>> deepOcean, Set<RegistryKey<Biome>> ocean, Set<RegistryKey<Biome>> beache, Set<RegistryKey<Biome>> river,
			Set<RegistryKey<Biome>> peak, Set<RegistryKey<Biome>> badland, Set<RegistryKey<Biome>> hill, Set<RegistryKey<Biome>> taiga,
			Set<RegistryKey<Biome>> jungle, Set<RegistryKey<Biome>> forest, Set<RegistryKey<Biome>> nether
	)
	{
		registrar.accept(UhcStructures.HONEY_WORKSHOP, BiomeKeys.FLOWER_FOREST);
		registrar.accept(UhcStructures.HONEY_WORKSHOP, BiomeKeys.PLAINS);
		registrar.accept(UhcStructures.HONEY_WORKSHOP, BiomeKeys.SNOWY_PLAINS);
		registrar.accept(UhcStructures.HONEY_WORKSHOP, BiomeKeys.SUNFLOWER_PLAINS);

		registrar.accept(UhcStructures.PLAIN_COTTAGE, BiomeKeys.PLAINS);

		BuiltinRegistries.BIOME.getEntries().forEach(entry -> {
			RegistryKey<Biome> key = entry.getKey();
			Biome biome = entry.getValue();
			if (SinglePieceLandStructure.canGenerateIn(biome))
			{
				registrar.accept(UhcStructures.ENDER_PYRAMID, key);
				if (forest.contains(key) || taiga.contains(key))
				{
					registrar.accept(UhcStructures.VILLAIN_HOUSE, key);
				}
				switch (biome.getPrecipitation())
				{
					case NONE:
						registrar.accept(UhcStructures.GREENHOUSE_DESERT, key);
						break;
					case RAIN:
						break;
					case SNOW:
						registrar.accept(UhcStructures.GREENHOUSE_SNOW, key);
						break;
				}
			}
		});
	}
}
