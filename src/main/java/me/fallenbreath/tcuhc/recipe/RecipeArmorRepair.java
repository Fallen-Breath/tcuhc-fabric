package me.fallenbreath.tcuhc.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RecipeArmorRepair extends SpecialCraftingRecipe
{
	public RecipeArmorRepair(Identifier id)
	{
		super(id);
	}

	@Override
	public boolean matches(CraftingInventory inv, World world)
	{
		return this.craft(inv) != ItemStack.EMPTY;
	}

	@Override
	public ItemStack craft(CraftingInventory inv)
	{
		ItemStack armor = null;
		for (int i = 0; i < inv.size(); ++i)
		{
			ItemStack itemstack = inv.getStack(i);
			if (itemstack.getItem() instanceof ArmorItem)
			{
				if (armor == null)
				{
					armor = itemstack;
				}
				else
				{
					return ItemStack.EMPTY;
				}
			}
		}
		if (armor == null)
		{
			return ItemStack.EMPTY;
		}
		ArmorItem armorItem = (ArmorItem)armor.getItem();
		int repairCnt = 0;
		for (int i = 0; i < inv.size(); ++i)
		{
			ItemStack itemstack = inv.getStack(i);
			if (!itemstack.isEmpty() && !(itemstack.getItem() instanceof ArmorItem))
			{
				if (armorItem.canRepair(armor, itemstack))
				{
					repairCnt++;
				}
				else
				{
					return ItemStack.EMPTY;
				}
			}
		}
		if (repairCnt == 0)
		{
			return ItemStack.EMPTY;
		}
		ItemStack result = armor.copy();
		result.setDamage(armor.getDamage() - repairCnt * armor.getMaxDamage() / 4);
		return result;
	}

	@Override
	public boolean fits(int width, int height)
	{
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return UhcRecipeSerializer.REPAIR_ARMOR;
	}
}
