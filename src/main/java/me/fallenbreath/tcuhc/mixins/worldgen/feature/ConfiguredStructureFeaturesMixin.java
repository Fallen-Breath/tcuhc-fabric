package me.fallenbreath.tcuhc.mixins.worldgen.feature;

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
	private static void allowBastionRemnantToGenerateInBasaltDeltas(BiConsumer<ConfiguredStructureFeature<?, ?>, RegistryKey<Biome>> registrar, CallbackInfo ci, Set<RegistryKey<Biome>> set, Set<RegistryKey<Biome>> set2, Set<RegistryKey<Biome>> set3, Set<RegistryKey<Biome>> set4, Set<RegistryKey<Biome>> set5, Set<RegistryKey<Biome>> set6, Set<RegistryKey<Biome>> set7, Set<RegistryKey<Biome>> set8, Set<RegistryKey<Biome>> set9, Set<RegistryKey<Biome>> set10, Set<RegistryKey<Biome>> set11)
	{
		// allow BastionRemnant to generate in basalt deltas
		register(registrar, BASTION_REMNANT, BiomeKeys.BASALT_DELTAS);
		// allow ocean shipwecks and buried treasures to generate in rivers
		register(registrar, SHIPWRECK, set4);
		register(registrar, BURIED_TREASURE, set4);
	}
}
