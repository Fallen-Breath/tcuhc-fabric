package me.fallenbreath.tcuhc.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.village.TradeOffers$EnchantBookFactory")
public abstract class TradeOffersEnchantBookFactoryMixin
{
	@ModifyArg(
			method = "create",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/village/TradeOffer;<init>(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;IIF)V"
			),
			index = 3
	)
	private int modifyEnchantBookMaxUse(int maxUses)
	{
		return 1;
	}
}
