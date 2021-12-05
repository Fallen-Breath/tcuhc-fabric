package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherFortressFeature.class)
public abstract class NetherFortressFeatureMixin
{
	@Inject(method = "canGenerate", at = @At("HEAD"), cancellable = true)
	private static void fortressGenerateIffXZAre0(StructureGeneratorFactory.Context<DefaultFeatureConfig> context, CallbackInfoReturnable<Boolean> cir)
	{
		cir.setReturnValue(context.chunkPos().equals(new ChunkPos(0, 0)));
	}
}
