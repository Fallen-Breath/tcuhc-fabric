package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureFeature.class)
public abstract class StructureFeatureMixin
{
	@Inject(method = "getStartChunk", at = @At("HEAD"), cancellable = true)
	private void fortressStartChunkIsAlways00(CallbackInfoReturnable<ChunkPos> cir)
	{
		StructureFeature<?> self = (StructureFeature<?>)(Object)this;
		if (self instanceof NetherFortressFeature)
		{
			cir.setReturnValue(new ChunkPos(0, 0));
		}
	}
}
