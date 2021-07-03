package me.fallenbreath.tcuhc.recipe;

import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.recipe.SpecialRecipeSerializer;

public class UhcRecipeSerializer
{
	public static final SpecialRecipeSerializer<RecipeArmorRepair> REPAIR_ARMOR = UhcRegistry.registerRecipeSerializer("crafting_repair_armor", new SpecialRecipeSerializer<>(RecipeArmorRepair::new));
	public static final SpecialRecipeSerializer<RecipeGoldenApple> GOLDEN_APPLE = UhcRegistry.registerRecipeSerializer("crafting_golden_apple", new SpecialRecipeSerializer<>(RecipeGoldenApple::new));

	public static void noop()
	{
	}
}
