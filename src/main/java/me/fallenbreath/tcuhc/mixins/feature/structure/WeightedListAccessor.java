package me.fallenbreath.tcuhc.mixins.feature.structure;

import net.minecraft.util.collection.WeightedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Random;

@Mixin(WeightedList.class)
public interface WeightedListAccessor<U>
{
	@Accessor
	List<WeightedList.Entry<U>> getEntries();

	@Accessor
	@Mutable
	void setRandom(Random random);
}
