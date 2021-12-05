package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import net.minecraft.world.gen.feature.ConfiguredFeatures;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(ConfiguredFeatures.class)
public abstract class DefaultBiomeFeaturesMixin
{
	/*
	 * // TC Plugin
	 * this.coalSize = 10;
	 * this.ironSize = 6;
	 * this.ironCount = 4;
	 * this.goldSize = 4;
	 * this.goldCount = 1;
	 * this.goldMaxHeight = 20;
	 * this.redstoneSize = 4;
	 * this.redstoneCount = 4;
	 * this.diamondSize = 4;
	 * this.lapisSize = 4;
	 * this.lapisCount = 2;
	 * this.lapisCenterHeight = 20;
	 * this.lapisSpread = 10;
	 */

	// should we still need to do this in 1.18?

//	@Unique
//	private static int calcOreCount(int baseCount)
//	{
//		int oreCnt = Options.instance.getIntegerOptionValue("oreFrequency");
//		return baseCount * oreCnt;
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_coal")), constant = @Constant(intValue = 17, ordinal = 0))
//	private static int coralSize(int value)
//	{
//		return 10;
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_iron")), constant = @Constant(intValue = 9, ordinal = 0))
//	private static int ironSize(int value)
//	{
//		return 6;
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_iron")), constant = @Constant(intValue = 20, ordinal = 0))
//	private static int ironCount(int value)
//	{
//		return calcOreCount(4);
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_gold")), constant = @Constant(intValue = 9, ordinal = 0))
//	private static int goldSize(int value)
//	{
//		return 4;
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_gold")), constant = @Constant(intValue = 2, ordinal = 0))
//	private static int goldCount(int value)
//	{
//		return calcOreCount(1);
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_gold")), constant = @Constant(intValue = 32, ordinal = 0))
//	private static int goldMaxHeight(int value)
//	{
//		return 20;
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_redstone")), constant = @Constant(intValue = 8, ordinal = 0))
//	private static int redstoneSize(int value)
//	{
//		return 4;
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_redstone")), constant = @Constant(intValue = 8, ordinal = 1))
//	private static int redstoneCount(int value)
//	{
//		return calcOreCount(4);
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_diamond")), constant = @Constant(intValue = 8, ordinal = 0))
//	private static int diamondSize(int value)
//	{
//		return 4;
//	}
//
//	@ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_diamond")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/ConfiguredFeatures;register(Ljava/lang/String;Lnet/minecraft/world/gen/feature/ConfiguredFeature;)Lnet/minecraft/world/gen/feature/ConfiguredFeature;", ordinal = 0), index = 1)
//	private static ConfiguredFeature<?, ?> diamondCount(ConfiguredFeature<?, ?> configuredFeature)
//	{
//		return configuredFeature.repeat(calcOreCount(1));
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_lapis")), constant = @Constant(intValue = 7, ordinal = 0))
//	private static int lapisSize(int value)
//	{
//		return 4;
//	}
//
//	@ModifyArg(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_lapis")), at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/feature/ConfiguredFeatures;register(Ljava/lang/String;Lnet/minecraft/world/gen/feature/ConfiguredFeature;)Lnet/minecraft/world/gen/feature/ConfiguredFeature;", ordinal = 0), index = 1)
//	private static ConfiguredFeature<?, ?> lapisCount(ConfiguredFeature<?, ?> configuredFeature)
//	{
//		return configuredFeature.repeat(calcOreCount(2));
//	}
//
//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_lapis")), constant = @Constant(intValue = 16, ordinal = 0))
//	private static int lapisCenterHeight(int value)
//	{
//		return 20;
//	}

//	@ModifyConstant(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ore_lapis")), constant = @Constant(intValue = 16, ordinal = 1))
//	private static int lapisSpread(int value)
//	{
//		return 10;
//	}
}
