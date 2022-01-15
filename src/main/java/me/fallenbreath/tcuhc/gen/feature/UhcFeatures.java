package me.fallenbreath.tcuhc.gen.feature;

import com.google.common.collect.ImmutableSet;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.*;

import java.util.Set;

public class UhcFeatures
{
	public static final PlacedFeature MERCHANTS = create("merchants", new MerchantsFeature(DefaultFeatureConfig.CODEC));
	public static final PlacedFeature BONUS_CHEST = create("bonus_chest", new BonusChestFeature(DefaultFeatureConfig.CODEC));
	public static final PlacedFeature ENDER_ALTAR = create("ender_altar", new EnderAltarFeature(DefaultFeatureConfig.CODEC));

	private static <F extends Feature<DefaultFeatureConfig>> PlacedFeature create(String name, F feature)
	{
		F feat = UhcRegistry.registerFeature(name, feature);
		ConfiguredFeature<DefaultFeatureConfig, ?> cf = UhcRegistry.registerConfiguredFeature(name, feat.configure(FeatureConfig.DEFAULT));
		return UhcRegistry.registerPlacedFeature(name, cf.withPlacement(
				SquarePlacementModifier.of(),
				PlacedFeatures.OCEAN_FLOOR_WG_HEIGHTMAP
		));
	}

	private static final Set<Block> VALUABLE_ORES = new ImmutableSet.Builder<Block>().
			add(Blocks.IRON_ORE).add(Blocks.DEEPSLATE_IRON_ORE).
			add(Blocks.GOLD_ORE).add(Blocks.DEEPSLATE_GOLD_ORE).
			add(Blocks.LAPIS_ORE).add(Blocks.DEEPSLATE_LAPIS_ORE).
			add(Blocks.DIAMOND_ORE).add(Blocks.DEEPSLATE_DIAMOND_ORE).
			add(Blocks.EMERALD_ORE).add(Blocks.DEEPSLATE_EMERALD_ORE).
			add(Blocks.NETHER_QUARTZ_ORE).
			build();

	public static boolean isValuableOreBlock(Block block)
	{
		return VALUABLE_ORES.contains(block);
	}
}
