package me.fallenbreath.tcuhc.mixins.feature.structure;

import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(StructureFeature.class)
public interface StructureFeatureAccessor
{
	@Accessor
	static Map<StructureFeature<?>, GenerationStep.Feature> getSTRUCTURE_TO_GENERATION_STEP()
	{
		throw new RuntimeException();
	}

	@Accessor
	@Mutable
	static void setLAND_MODIFYING_STRUCTURES(List<StructureFeature<?>> list)
	{
		throw new RuntimeException();
	}
}
