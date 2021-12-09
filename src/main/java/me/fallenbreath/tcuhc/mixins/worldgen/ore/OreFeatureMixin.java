package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import me.fallenbreath.tcuhc.gen.UhcFeatures;
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

import static net.minecraft.world.gen.feature.Feature.isExposedToAir;

@Mixin(OreFeature.class)
public abstract class OreFeatureMixin
{
	@Inject(method = "shouldPlace", at = @At("HEAD"), cancellable = true)
	private static void uhcValuableOreNeverHidesInBlocks(BlockState state, Function<BlockPos, BlockState> posToState, Random random, OreFeatureConfig config, OreFeatureConfig.Target target, BlockPos.Mutable pos, CallbackInfoReturnable<Boolean> cir)
	{
		if (UhcFeatures.isValuableOreBlock(target.state.getBlock()))
		{
			if (!isExposedToAir(posToState, pos))
			{
				cir.setReturnValue(false);
			}
		}
	}

	@Inject(
			method = "shouldPlace",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/gen/feature/OreFeature;isExposedToAir(Ljava/util/function/Function;Lnet/minecraft/util/math/BlockPos;)Z"
			),
			cancellable = true
	)
	private static void uhcValuableOreLovesToBeExposedToAir(BlockState state, Function<BlockPos, BlockState> posToState, Random random, OreFeatureConfig config, OreFeatureConfig.Target target, BlockPos.Mutable pos, CallbackInfoReturnable<Boolean> cir)
	{
		if (UhcFeatures.isValuableOreBlock(target.state.getBlock()))
		{
			if (random.nextFloat() < config.discardOnAirChance / 2)
			{
				cir.setReturnValue(true);
			}
		}
	}
}
