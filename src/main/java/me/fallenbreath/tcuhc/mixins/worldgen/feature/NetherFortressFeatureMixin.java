package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(NetherFortressFeature.class)
public abstract class NetherFortressFeatureMixin
{
	@Inject(method = "shouldStartAt", at = @At("HEAD"), cancellable = true)
	private void fortressGenerateIffXZAre0(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkX, int chunkZ, Biome biome, CallbackInfoReturnable<Boolean> cir)
	{
		cir.setReturnValue(chunkX == 0 && chunkZ == 0);
	}
}
