package me.fallenbreath.tcuhc.mixins.worldgen.world;

import net.minecraft.world.gen.NoiseColumnSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseColumnSampler.class)
public abstract class NoiseColumnSamplerMixin
{
	/**
	 * Does not eliminate 100% ocean like terrains, but the percentage of ocean is much smaller
	 */
	@Inject(
			method = "sampleContinentalnessNoise",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/util/math/noise/DoublePerlinNoiseSampler;sample(DDD)D"
					)
			),
			at = @At(value = "RETURN", ordinal = 0),
			cancellable = true
	)
	private void iHateOceansSoPleaseMakeTheReturnValueLarger(CallbackInfoReturnable<Double> cir)
	{
		double x = cir.getReturnValue();
		cir.setReturnValue(0.6 * x + 0.4);
	}
}
