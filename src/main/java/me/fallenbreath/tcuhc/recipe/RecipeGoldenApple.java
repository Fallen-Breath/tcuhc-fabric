package me.fallenbreath.tcuhc.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtInt;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RecipeGoldenApple extends SpecialCraftingRecipe
{
	public RecipeGoldenApple(Identifier id)
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
		boolean hasApple = false;
		int goldCnt = 0;
		for (int i = 0; i < inv.size(); ++i)
		{
			ItemStack itemstack = inv.getStack(i);
			if (itemstack.getItem() == Items.APPLE)
			{
				if (!hasApple)
				{
					hasApple = true;
				}
				else
				{
					return ItemStack.EMPTY;
				}
			}
			else if (itemstack.getItem() == Items.GOLD_INGOT)
			{
				goldCnt++;
			}
		}
		if (hasApple && goldCnt > 0 && goldCnt % 2 == 0)
		{
			int level = goldCnt / 2;
			ItemStack res = new ItemStack(Items.GOLDEN_APPLE);
			if (level != 4)
			{
				res.getOrCreateTag().put("level", NbtInt.of(level));
			}
			return res;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean fits(int width, int height)
	{
		return width >= 3 && height >= 3;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return UhcRecipeSerializer.GOLDEN_APPLE;
	}
}
