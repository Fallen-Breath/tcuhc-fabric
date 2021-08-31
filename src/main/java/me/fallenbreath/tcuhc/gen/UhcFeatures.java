package me.fallenbreath.tcuhc.gen;

import me.fallenbreath.tcuhc.mixins.worldgen.ore.OreFeatureConfigAccessor;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

public class UhcFeatures
{
	public static final Feature<OreFeatureConfig> VALUABLE_ORE = UhcRegistry.registerFeature("valuable_ore", new ValuableOreFeature(OreFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> MERCHANTS = UhcRegistry.registerFeature("merchants", new MerchantsFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> BONUS_CHEST = UhcRegistry.registerFeature("bonus_chest", new BonusChestFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> ENDER_ALTAR = UhcRegistry.registerFeature("ender_altar", new EnderAltarFeature(DefaultFeatureConfig::deserialize));

	public static boolean isValuableOreBlock(Block block)
	{
		return block == Blocks.IRON_ORE || block == Blocks.GOLD_ORE || block == Blocks.DIAMOND_ORE || block == Blocks.LAPIS_ORE || block == Blocks.NETHER_QUARTZ_ORE;
	}

	public static boolean shouldModifyToValuableOre(Feature<?> feature, FeatureConfig featureConfig)
	{
		// TODO: more custom modification like EmeraldOre or other ores manipulating
		if (feature == Feature.ORE && featureConfig instanceof OreFeatureConfig)
		{
			Block block = ((OreFeatureConfigAccessor)featureConfig).getState().getBlock();
			return isValuableOreBlock(block);
		}
		return false;
	}
}
