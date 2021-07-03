package me.fallenbreath.tcuhc.mixins.worldgen.ore;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OreFeatureConfig.class)
public interface OreFeatureConfigAccessor
{
	@Accessor
	BlockState getState();
}
