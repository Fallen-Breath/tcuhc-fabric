package me.fallenbreath.tcuhc.mixins.loot;

import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.util.LootTableUtil;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(LootManager.class)
public abstract class LootManagerMixin
{
	@Shadow private Map<Identifier, LootTable> suppliers;

	@Inject(method = "apply", at = @At("RETURN"))
	private void qwq(CallbackInfo ci)
	{
		LootPool uhcAppleDrop = LootTableUtil.getLeavesDropAppleLootPool();
		this.suppliers.forEach((id, table) -> {
			Block block = Registry.BLOCK.get(new Identifier(id.getNamespace(), id.getPath().replace("blocks/", "")));
			if (block instanceof LeavesBlock)
			{
				LootTableAccessor tableAccessor = (LootTableAccessor)table;
				List<LootPool> lootPools = Lists.newArrayList(tableAccessor.getPools());
				boolean modified = false;
				for (int i = 0; i < lootPools.size(); i++)
				{
					LootEntry[] lootEntries = ((LootPoolAccessor)lootPools.get(i)).getEntries();
					if (lootEntries.length == 1 && lootEntries[0] instanceof ItemEntry && ((ItemEntryAccessor)lootEntries[0]).getItem() == Items.APPLE)
					{
						lootPools.set(i, uhcAppleDrop);
						modified = true;
					}
				}
				if (!modified)
				{
					lootPools.add(uhcAppleDrop);
				}
				tableAccessor.setPools(lootPools.toArray(new LootPool[0]));
			}
		});
	}
}
