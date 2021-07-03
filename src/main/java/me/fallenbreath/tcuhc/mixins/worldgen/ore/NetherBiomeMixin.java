package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import me.fallenbreath.tcuhc.gen.UhcFeatures;
import net.minecraft.world.biome.NetherBiome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(NetherBiome.class)
public abstract class NetherBiomeMixin
{
	@ModifyArgs(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/biome/NetherBiome;configureFeature(Lnet/minecraft/world/gen/feature/Feature;Lnet/minecraft/world/gen/feature/FeatureConfig;Lnet/minecraft/world/gen/decorator/Decorator;Lnet/minecraft/world/gen/decorator/DecoratorConfig;)Lnet/minecraft/world/gen/feature/ConfiguredFeature;"
			)
	)
	private void modifyOreFeature(Args args)
	{
		if (UhcFeatures.shouldModifyToValuableOre(args.get(0), args.get(1)))
		{
			args.set(0, UhcFeatures.VALUABLE_ORE);
		}
	}
}
