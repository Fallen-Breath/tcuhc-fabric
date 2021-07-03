package me.fallenbreath.tcuhc.mixins.entity;

import me.fallenbreath.tcuhc.interfaces.IPlayerInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements IPlayerInventory
{
	@Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

	@Shadow @Final public PlayerEntity player;

	/**
	 * Like {@link PlayerInventory#dropAll()}, but doesn't remove the actual item
	 */
	@Override
	public void dropAllItemsWithoutClear()
	{
		for (List<ItemStack> list : this.combinedInventory)
		{
			for (ItemStack itemstack : list)
			{
				if (!itemstack.isEmpty())
				{
					this.player.dropItem(itemstack.copy(), true, false);
				}
			}
		}
	}
}
