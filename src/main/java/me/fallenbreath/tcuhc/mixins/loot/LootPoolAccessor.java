package me.fallenbreath.tcuhc.mixins.loot;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface LootPoolAccessor
{
	@Accessor
	LootPoolEntry[] getEntries();

	@Accessor
	void setEntries(LootPoolEntry[] entries);
}
