package me.fallenbreath.tcuhc.mixins.worldgen.world;

import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaBiomeParameters.class)
public abstract class VanillaBiomeParametersMixin
{
	@Inject(method = "writeOceanBiomes", at = @At("HEAD"), cancellable = true)
	private void noOcean(CallbackInfo ci)
	{
		ci.cancel();
	}
}
