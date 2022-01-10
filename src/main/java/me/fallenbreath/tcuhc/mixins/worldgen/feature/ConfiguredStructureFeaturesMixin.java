package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import me.fallenbreath.tcuhc.gen.structure.EnderPyramidStructure;
import me.fallenbreath.tcuhc.gen.structure.GreenhouseStructure;
import me.fallenbreath.tcuhc.gen.structure.UhcStructures;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.feature.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;
import java.util.function.BiConsumer;

@Mixin(ConfiguredStructureFeatures.class)
public abstract class ConfiguredStructureFeaturesMixin
{
	@Shadow @Final private static ConfiguredStructureFeature<StructurePoolFeatureConfig, ? extends StructureFeature<StructurePoolFeatureConfig>> BASTION_REMNANT;

	@Shadow @Final private static ConfiguredStructureFeature<ShipwreckFeatureConfig, ? extends StructureFeature<ShipwreckFeatureConfig>> SHIPWRECK;

	@Shadow @Final private static ConfiguredStructureFeature<ProbabilityConfig, ? extends StructureFeature<ProbabilityConfig>> BURIED_TREASURE;

	@Shadow
	private static void register(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, ConfiguredStructureFeature<?, ?> feature, RegistryKey<Biome> biome)
	{
	}

	@Shadow
	private static void register(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, ConfiguredStructureFeature<?, ?> feature, Set<RegistryKey<Biome>> biomes)
	{
	}

	@Inject(method = "registerAll", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void allowBastionRemnantToGenerateInBasaltDeltas(
			BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, CallbackInfo ci,
			Set<RegistryKey<Biome>> deepOcean, Set<RegistryKey<Biome>> ocean, Set<RegistryKey<Biome>> beache, Set<RegistryKey<Biome>> river,
			Set<RegistryKey<Biome>> peak, Set<RegistryKey<Biome>> badland, Set<RegistryKey<Biome>> hill, Set<RegistryKey<Biome>> taiga,
			Set<RegistryKey<Biome>> jungle, Set<RegistryKey<Biome>> forest, Set<RegistryKey<Biome>> nether
	)
	{
		// ======== Vanilla Structure tweaks  ========

		// allow BastionRemnant to generate in basalt deltas
		register(registrar, BASTION_REMNANT, BiomeKeys.BASALT_DELTAS);
		// allow ocean shipwecks and buried treasures to generate in rivers
		register(registrar, SHIPWRECK, river);
		register(registrar, BURIED_TREASURE, river);

		//  ======== UHC Structures ========
		BuiltinRegistries.BIOME.getEntries().forEach(entry -> {
			RegistryKey<Biome> key = entry.getKey();
			Biome biome = entry.getValue();
			if (EnderPyramidStructure.canGenerateIn(biome))
			{
				register(registrar, UhcStructures.ENDER_PYRAMID, key);
			}
			if (GreenhouseStructure.canGenerateIn(biome))
			{
				switch (biome.getPrecipitation())
				{
					case NONE:
						register(registrar, UhcStructures.GREENHOUSE_DESERT, key);
						break;
					case RAIN:
						break;
					case SNOW:
						register(registrar, UhcStructures.GREENHOUSE_SNOW, key);
						break;
				}
			}
		});
	}
}
