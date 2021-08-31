package me.fallenbreath.tcuhc.mixins.loot;

import me.fallenbreath.tcuhc.interfaces.LootTableAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

// cannot use an Accessor or IllegalAccessError will appear, idk why
@Mixin(LootTable.class)
public abstract class LootTableMixin implements LootTableAccessor
{
	@Mutable
	@Shadow @Final LootPool[] pools;

	@Override
	public LootPool[] getPools()
	{
		return this.pools;
	}

	@Override
	public void setPools(LootPool[] lootPools)
	{
		this.pools = lootPools;
	}
}
