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
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Map;
import java.util.Set;

import static net.minecraft.world.biome.Biome.Category.*;

public class UhcStructures
{
	public static final Set<Biome.Category> CONTINENT_BIOMES = ImmutableSet.of(TAIGA, EXTREME_HILLS, JUNGLE, MESA, PLAINS, SAVANNA, ICY, FOREST, DESERT, SWAMP, MUSHROOM, MOUNTAIN);

	public static final ConfiguredStructureFeature<DefaultFeatureConfig, ?> ENDER_PYRAMID = create("ender_pyramid", new EnderPyramidStructure(DefaultFeatureConfig.CODEC), EnderPyramidStructure.CONFIG);

	public static void load()
	{
		// no op
	}

	private static <F extends StructureFeature<DefaultFeatureConfig>> ConfiguredStructureFeature<DefaultFeatureConfig, ?> create(String name, F structure, StructureConfig config)
	{
		F struct = UhcRegistry.registerStructure(name, structure, GenerationStep.Feature.SURFACE_STRUCTURES);
		Map<StructureFeature<?>, StructureConfig> map = Maps.newHashMap(StructuresConfigAccessor.getDEFAULT_STRUCTURES());
		map.put(struct, config);
		StructuresConfigAccessor.setDEFAULT_STRUCTURES(new ImmutableMap.Builder<StructureFeature<?>, StructureConfig>().putAll(map).build());
		return UhcRegistry.registerConfiguredStructure(name, struct.configure(DefaultFeatureConfig.INSTANCE));
	}
}
