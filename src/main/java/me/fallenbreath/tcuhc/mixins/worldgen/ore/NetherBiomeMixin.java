package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import me.fallenbreath.tcuhc.gen.UhcFeatures;
import net.minecraft.world.biome.NetherBiome;
import net.minecraft.world.gen.feature.Feature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(NetherBiome.class)
public abstract class NetherBiomeMixin
{
	@Redirect(
			method = "<init>",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lnet/minecraft/block/Blocks;NETHER_QUARTZ_ORE:Lnet/minecraft/block/Block;"
					)
			),
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/gen/feature/Feature;ORE:Lnet/minecraft/world/gen/feature/Feature;",
					ordinal = 0
			)
	)
	private Feature<?> modifyOreFeature()
	{
		return UhcFeatures.VALUABLE_ORE;
	}
}
