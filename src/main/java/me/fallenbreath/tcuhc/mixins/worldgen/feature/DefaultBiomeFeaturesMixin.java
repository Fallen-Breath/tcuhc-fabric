package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import me.fallenbreath.tcuhc.gen.UhcFeatures;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultBiomeFeatures.class)
public abstract class DefaultBiomeFeaturesMixin
{
	private static void addSurfaceFeature(GenerationSettings.Builder builder, PlacedFeature feature)
	{
		builder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, feature);
	}

	@Inject(method = "addDefaultOres(Lnet/minecraft/world/biome/GenerationSettings$Builder;Z)V", at = @At("TAIL"))
	private static void addUhcFeatures(GenerationSettings.Builder builder, boolean largeCopperOreBlob, CallbackInfo ci)
	{
		addSurfaceFeature(builder, UhcFeatures.MERCHANTS);
		addSurfaceFeature(builder, UhcFeatures.BONUS_CHEST);
		addSurfaceFeature(builder, UhcFeatures.ENDER_ALTAR);
	}
}
