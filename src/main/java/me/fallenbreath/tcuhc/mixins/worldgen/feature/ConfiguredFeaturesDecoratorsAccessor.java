package me.fallenbreath.tcuhc.mixins.worldgen.feature;

import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net/minecraft/world/gen/feature/ConfiguredFeatures$Decorators")
public interface ConfiguredFeaturesDecoratorsAccessor
{
	@Accessor
	static ConfiguredDecorator<HeightmapDecoratorConfig> getTOP_SOLID_HEIGHTMAP()
	{
		return null;
	}
}
