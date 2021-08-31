package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import me.fallenbreath.tcuhc.gen.UhcFeatures;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public abstract class DefaultBiomeFeaturesMixin
{
	private static void addSurfaceFeature(Biome biome, Feature<DefaultFeatureConfig> feature)
	{
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, feature.configure(FeatureConfig.DEFAULT).createDecoratedFeature(Decorator.NOPE.configure(DecoratorConfig.DEFAULT)));
	}

	@Inject(method = "addDefaultStructures", at = @At("TAIL"))
	private static void addUhcFeatures(Biome biome, CallbackInfo ci)
	{
		addSurfaceFeature(biome, UhcFeatures.MERCHANTS);
		addSurfaceFeature(biome, UhcFeatures.BONUS_CHEST);
		addSurfaceFeature(biome, UhcFeatures.ENDER_ALTAR);
	}
}
