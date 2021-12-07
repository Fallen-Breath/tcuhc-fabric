package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.options.Options;
import me.fallenbreath.tcuhc.util.AverageIntProvider;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.feature.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlacedFeatures.class)
public abstract class PlacedFeaturesMixin
{
	/**
	 * oreFrequency = 4   ->   CountPlacementModifier.of(1.0)
	 */
	private static final float ADJUST_RATIO = 0.25F;

	/**
	 * See comments in {@link DefaultBiomeFeaturesMixin}
	 */
	private static final List<String> ORES_TO_BE_MODIFIED_COUNT = new ImmutableList.Builder<String>().
//			add("ore_coal").
//			add("ore_copper").
			add("ore_lapis").
			add("ore_iron").
//			add("ore_redstone").
			add("ore_gold").
			add("ore_diamond").
//			add("ore_emerald").
//			add("ore_debris").
			build();

	/**
	 * Hooks here so we can modify placements in {@link net.minecraft.world.gen.feature.OrePlacedFeatures} easier
	 */
	@Inject(method = "register", at = @At("HEAD"))
	private static void uhcModifyOreFrequency(String id, PlacedFeature placedFeature, CallbackInfoReturnable<PlacedFeature> cir)
	{
		if (id.startsWith("ore_") && !id.endsWith("_nether") && ORES_TO_BE_MODIFIED_COUNT.stream().anyMatch(id::startsWith))
		{
			PlacedFeatureAccessor pfa = (PlacedFeatureAccessor)placedFeature;
			ConfiguredFeature<?, ?> configuredFeature = pfa.getFeature().get();
			if (configuredFeature.feature == Feature.ORE && configuredFeature.config instanceof OreFeatureConfig)
			{
				float frequency = Options.instance.getIntegerOptionValue("oreFrequency") * ADJUST_RATIO;
				List<PlacementModifier> list = Lists.newArrayList();
				list.add(CountPlacementModifier.of(new AverageIntProvider(frequency)));
				list.addAll(pfa.getPlacementModifiers());
				pfa.setPlacementModifiers(list);
			}
		}
	}
}
