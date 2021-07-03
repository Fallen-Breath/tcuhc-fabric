package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import me.fallenbreath.tcuhc.gen.UhcFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public abstract class DefaultBiomeFeaturesMixin
{
	@Inject(method = "addDefaultStructures", at = @At("TAIL"))
	private static void addUhcFeatures(Biome biome, CallbackInfo ci)
	{
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(UhcFeatures.MERCHANTS, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(UhcFeatures.BONUS_CHEST, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(UhcFeatures.ENDER_ALTAR, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
	}
}
