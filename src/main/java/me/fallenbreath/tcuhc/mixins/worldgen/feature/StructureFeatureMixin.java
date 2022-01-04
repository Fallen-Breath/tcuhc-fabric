package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.util.UhcWorldData;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.BastionRemnantFeature;
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
		UhcWorldData.StructureType type = UhcGameManager.instance.getWorldData().netherFortressType;
		ChunkPos zero = new ChunkPos(0, 0);

		if (self instanceof NetherFortressFeature && type == UhcWorldData.StructureType.NETHER_FORTRESS)
		{
			cir.setReturnValue(zero);
		}
		if (self instanceof BastionRemnantFeature && type == UhcWorldData.StructureType.BASTION_REMNANT)
		{
			cir.setReturnValue(zero);
		}
	}
}
