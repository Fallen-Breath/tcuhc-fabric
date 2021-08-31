package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import me.fallenbreath.tcuhc.interfaces.IOreFeature;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;
import java.util.function.Function;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin implements IOreFeature
{
	private static final ThreadLocal<IOreFeature> currentInstance = new ThreadLocal<>();

	@Inject(method = "generateVeinPart", at = @At("HEAD"))
	private void recordCurrentInstance(CallbackInfoReturnable<Boolean> cir)
	{
		currentInstance.set(this);
	}

	@Inject(method = "shouldPlace", at = @At("HEAD"), cancellable = true)
	private static void checkingForValuableOreType(BlockState state, Function<BlockPos, BlockState> posToState, Random random, OreFeatureConfig config, OreFeatureConfig.Target target, BlockPos.Mutable pos, CallbackInfoReturnable<Boolean> cir)
	{
		if (!currentInstance.get().isValidPositionForValuableOre(posToState, pos, state))
		{
			cir.setReturnValue(false);
		}
	}
}
