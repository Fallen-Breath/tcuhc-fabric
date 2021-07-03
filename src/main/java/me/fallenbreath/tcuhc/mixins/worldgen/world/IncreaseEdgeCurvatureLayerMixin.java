package me.fallenbreath.tcuhc.mixins.worldgen.world;

import net.minecraft.world.biome.layer.IncreaseEdgeCurvatureLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IncreaseEdgeCurvatureLayer.class)
public abstract class IncreaseEdgeCurvatureLayerMixin
{
	@Inject(method = "sample", at = @At("HEAD"), cancellable = true)
	private void noOceanPlease(LayerRandomnessSource context, int sw, int se, int ne, int nw, int center, CallbackInfoReturnable<Integer> cir)
	{
		cir.setReturnValue(center);
	}
}
