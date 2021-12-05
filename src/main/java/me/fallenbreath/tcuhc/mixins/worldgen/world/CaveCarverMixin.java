package me.fallenbreath.tcuhc.mixins.worldgen.world;

import net.minecraft.world.gen.carver.CaveCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(CaveCarver.class)
public abstract class CaveCarverMixin
{
	@Shadow protected abstract int getMaxCaveCount();

	@Redirect(
			method = "carve(Lnet/minecraft/world/gen/carver/CarverContext;Lnet/minecraft/world/gen/carver/CaveCarverConfig;Lnet/minecraft/world/chunk/Chunk;Ljava/util/function/Function;Ljava/util/Random;Lnet/minecraft/world/gen/chunk/AquiferSampler;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/gen/carver/CarvingMask;)Z",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Random;nextInt(I)I",
					ordinal = 2
			)
	)
	private int customRandomizer(Random random, int bound)
	{
		if (random.nextInt(2) == 0)
		{
			return random.nextInt(this.getMaxCaveCount());
		}
		return bound;
	}
}
