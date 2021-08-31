package me.fallenbreath.tcuhc.mixins.worldgen.world;

import me.fallenbreath.tcuhc.gen.PureContinentLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.ContinentLayer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeLayers.class)
public abstract class BiomeLayersMixin
{
	/**
	 * net.minecraft.world.gen.layer.GenLayer#initializeAllBiomeGenerators in 1.12 MCP
	 */
	@Redirect(
			method = "build(ZIILjava/util/function/LongFunction;)Lnet/minecraft/world/biome/layer/util/LayerFactory;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/biome/layer/ContinentLayer;create(Lnet/minecraft/world/biome/layer/util/LayerSampleContext;)Lnet/minecraft/world/biome/layer/util/LayerFactory;"
			)
	)
	private static <R extends LayerSampler> LayerFactory<R> thereIsNoOcean(ContinentLayer continentLayer, LayerSampleContext<R> context)
	{
		return PureContinentLayer.INSTANCE.create(context);
	}
}
