package me.fallenbreath.tcuhc.mixins.recipe;

import me.fallenbreath.tcuhc.recipe.UhcRecipeSerializer;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Registry.class)
public abstract class RegistryMixin
{
	static
	{
		UhcRecipeSerializer.noop();
	}
}
