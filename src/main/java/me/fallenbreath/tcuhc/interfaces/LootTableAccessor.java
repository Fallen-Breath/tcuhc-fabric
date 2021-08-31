package me.fallenbreath.tcuhc.interfaces;

import net.minecraft.loot.LootPool;

public interface LootTableAccessor
{
	LootPool[] getPools();

	void setPools(LootPool[] lootPools);
}
