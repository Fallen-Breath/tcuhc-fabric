package me.fallenbreath.tcuhc.mixins.worldgen.world;

import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StructuresConfig.class)
public abstract class StructuresConfigMixin
{
	@ModifyArg(
			method = "<clinit>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/gen/chunk/StrongholdConfig;<init>(III)V"
			),
			index = 0
	)
	private static int modifyDistance(int distance)
	{
		// 32 -> 12
		// see net.minecraft.world.gen.chunk.ChunkGenerator.generateStrongholdPositions for stronghold generation
		// so the first ring of stronghold will be inside 1000 blocks
		return 12;
	}
}
