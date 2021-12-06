package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.options.Options;
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
	private static final List<String> REAL_ORE_ID_PREFIXES = new ImmutableList.Builder<String>().
			add("ore_coal").
			add("ore_copper").
			add("ore_lapis").
			add("ore_iron").
			add("ore_redstone").
			add("ore_gold").
			add("ore_diamond").
			add("ore_emerald").
			add("ore_quartz").
			add("ore_debris").
			build();

	@Inject(method = "register", at = @At("HEAD"))
	private static void uhcModifyOreFrequency(String id, PlacedFeature placedFeature, CallbackInfoReturnable<PlacedFeature> cir)
	{
		if (id.startsWith("ore_") && REAL_ORE_ID_PREFIXES.stream().anyMatch(id::startsWith))
		{
			PlacedFeatureAccessor pfa = (PlacedFeatureAccessor)placedFeature;
			ConfiguredFeature<?, ?> configuredFeature = pfa.getFeature().get();
			if (configuredFeature.feature == Feature.ORE && configuredFeature.config instanceof OreFeatureConfig)
			{
				int frequency = Options.instance.getIntegerOptionValue("oreFrequency");
				List<PlacementModifier> list = Lists.newArrayList();
				list.add(CountPlacementModifier.of(frequency));
				list.addAll(pfa.getPlacementModifiers());
				pfa.setPlacementModifiers(list);
				UhcGameManager.LOG.debug("Append count modifier {} for ore generation {}", frequency, id);
			}
		}
	}
}
