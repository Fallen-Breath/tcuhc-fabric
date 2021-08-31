package me.fallenbreath.tcuhc.mixins.loot;

import me.fallenbreath.tcuhc.interfaces.LootPoolAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

// cannot use an Accessor or IllegalAccessError will appear, idk why
@Mixin(LootPool.class)
public abstract class LootPoolMixin implements LootPoolAccessor
{
	@Mutable
	@Shadow @Final LootPoolEntry[] entries;

	public LootPoolEntry[] getEntries()
	{
		return this.entries;
	}

	public void setEntries(LootPoolEntry[] entries)
	{
		this.entries = entries;
	}
}
