package me.fallenbreath.tcuhc.mixins.worldgen.structure;

import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.feature.ShipwreckFeature;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShipwreckFeature.class)
public abstract class ShipwreckFeatureMixin
{
	@Inject(method = "canGenerate", at = @At("HEAD"), cancellable = true)
	private static void dontGenerateTooHighWhenInRiver(StructureGeneratorFactory.Context<ShipwreckFeatureConfig> context, CallbackInfoReturnable<Boolean> cir)
	{
		int x = context.chunkPos().getCenterX();
		int z = context.chunkPos().getCenterZ();
		int y = context.chunkGenerator().getHeightInGround(x, z, Heightmap.Type.OCEAN_FLOOR_WG, context.world());
		Biome biome = context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(y), BiomeCoords.fromBlock(z));
		// Biome.Category.RIVER equals to BiomeKeys.RIVER + BiomeKeys.FROZEN_RIVER
		if (biome.getCategory() == Biome.Category.RIVER)
		{
			if (context.world() instanceof World)
			{
				World world = (World)context.world();
				if (y > world.getSeaLevel() - 7)
				{
					cir.setReturnValue(false);
				}
			}
		}
	}
}
