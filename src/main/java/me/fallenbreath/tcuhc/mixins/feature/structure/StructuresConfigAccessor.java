package me.fallenbreath.tcuhc.mixins.feature.structure;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StructuresConfig.class)
public interface StructuresConfigAccessor
{
	@Accessor
	static ImmutableMap<StructureFeature<?>, StructureConfig> getDEFAULT_STRUCTURES()
	{
		throw new RuntimeException();
	}

	@Accessor
	@Mutable
	static void setDEFAULT_STRUCTURES(ImmutableMap<StructureFeature<?>, StructureConfig> defaultStructures)
	{
		throw new RuntimeException();
	}
}
