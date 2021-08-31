package me.fallenbreath.tcuhc.interfaces;

import net.minecraft.loot.entry.LootPoolEntry;

public interface LootPoolAccessor
{
	LootPoolEntry[] getEntries();

	void setEntries(LootPoolEntry[] entries);
}
