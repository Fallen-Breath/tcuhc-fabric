package me.fallenbreath.tcuhc.mixins.worldgen.structure;

import me.fallenbreath.tcuhc.gen.structure.UhcStructures;
import net.minecraft.world.gen.chunk.StructuresConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StructuresConfig.class)
public abstract class StructuresConfigMixin
{
	@Inject(method = "<clinit>", at = @At("TAIL"))
	private static void loadUhcStructures(CallbackInfo ci)
	{
		UhcStructures.load();
	}
}
