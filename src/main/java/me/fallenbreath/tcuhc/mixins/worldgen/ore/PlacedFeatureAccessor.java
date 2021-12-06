package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.function.Supplier;

@Mixin(PlacedFeature.class)
public interface PlacedFeatureAccessor
{
	@Accessor
	Supplier<ConfiguredFeature<?, ?>> getFeature();

	@Accessor
	List<PlacementModifier> getPlacementModifiers();

	@Accessor @Mutable
	void setPlacementModifiers(List<PlacementModifier> placementModifiers);
}
