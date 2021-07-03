package me.fallenbreath.tcuhc.mixins.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin
{
	@Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
	private void modifyMaxUseTimeForGApples(ItemStack stack, CallbackInfoReturnable<Integer> cir)
	{
		if (stack.getItem() == Items.GOLDEN_APPLE || stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE)
		{
			cir.setReturnValue(10);
		}
	}
}
