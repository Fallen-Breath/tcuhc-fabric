package me.fallenbreath.tcuhc.mixins.block;

import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.interfaces.LootPoolAccessor;
import me.fallenbreath.tcuhc.interfaces.LootTableAccessor;
import me.fallenbreath.tcuhc.mixins.loot.ItemEntryAccessor;
import me.fallenbreath.tcuhc.util.LootTableUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.Items;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;
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
	@Shadow private Map<Identifier, LootTable> tables;

	@Inject(method = "apply", at = @At("TAIL"))
	private void uhcBlockLootAdjust(CallbackInfo ci)
	{
		Logger logger = UhcGameManager.LOG;
		LootPool uhcAppleDrop = LootTableUtil.getUhcLootPool("apple");
		LootPoolEntry uhcGlowStoneDrop = LootTableUtil.getUhcLootEntry("glowstone");
		LootPoolEntry uhcLapisOreDrop = LootTableUtil.getUhcLootEntry("lapis_ore");

		this.tables.forEach((id, table) -> {
			Block block = Registry.BLOCK.get(new Identifier(id.getNamespace(), id.getPath().replace("blocks/", "")));
			LootTableAccessor tableAccessor = (LootTableAccessor)table;
			if (block instanceof LeavesBlock)
			{
				List<LootPool> lootPools = Lists.newArrayList(tableAccessor.getPools());
				boolean modified = false;
				for (int i = 0; i < lootPools.size(); i++)
				{
					LootPoolEntry[] lootEntries = ((LootPoolAccessor)lootPools.get(i)).getEntries();
					if (lootEntries.length == 1 && lootEntries[0] instanceof ItemEntry && ((ItemEntryAccessor)lootEntries[0]).getItem() == Items.APPLE)
					{
						lootPools.set(i, uhcAppleDrop);
						logger.info("Modified apple drop of {}", block);
						modified = true;
					}
				}
				if (!modified)
				{
					lootPools.add(uhcAppleDrop);
					logger.info("Appended apple drop to {}", block);
				}
				tableAccessor.setPools(lootPools.toArray(new LootPool[0]));
			}
			else if (block == Blocks.GLOWSTONE || block == Blocks.LAPIS_ORE)
			{
				if (tableAccessor.getPools().length == 1)
				{
					LootPoolEntry lootEntry = block == Blocks.GLOWSTONE ? uhcGlowStoneDrop : uhcLapisOreDrop;
					((LootPoolAccessor)tableAccessor.getPools()[0]).setEntries(new LootPoolEntry[]{lootEntry});
				}
				logger.info("Modified drop of {}", block);
			}
		});
	}
}
