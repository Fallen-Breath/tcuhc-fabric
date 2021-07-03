package me.fallenbreath.tcuhc.mixins.recipe;

import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.util.UhcRegistry;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SynchronizeRecipesS2CPacket.class)
public abstract class SynchronizeRecipesS2CPacketMixin
{
	@Shadow private List<Recipe<?>> recipes;

	@Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("TAIL"))
	private void dontWriteUhcRecipes(CallbackInfo ci)
	{
		List<Recipe<?>> filteredRecipes = Lists.newArrayList();
		for (Recipe<?> recipe : this.recipes)
		{
			if (!UhcRegistry.getRecipeSerializers().contains(recipe.getSerializer()))
			{
				filteredRecipes.add(recipe);
			}
		}
		this.recipes = filteredRecipes;
	}
}
