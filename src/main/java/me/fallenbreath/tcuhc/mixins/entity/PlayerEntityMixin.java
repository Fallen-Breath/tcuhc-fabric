package me.fallenbreath.tcuhc.mixins.entity;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.interfaces.IPlayerInventory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
	/**
	 * Added tag check for KING mode's king's crown item
	 */
	@ModifyArg(
			method = "vanishCursedItems",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasVanishingCurse(Lnet/minecraft/item/ItemStack;)Z"
			),
			index = 0
	)
	private ItemStack kingsCrownWillNeverVanish(ItemStack itemStack)
	{
		if (itemStack.getTag() != null && itemStack.getTag().contains("KingsCrown"))
		{
			return ItemStack.EMPTY;
		}
		return itemStack;
	}

	/**
	 * TC Plugin: Kill entity hook
	 */
	@Inject(method = "onKilledOther", at = @At("TAIL"))
	private void onKill(ServerWorld serverWorld, LivingEntity livingEntity, CallbackInfo ci)
	{
		UhcGameManager.instance.getUhcPlayerManager().getGamePlayer((PlayerEntity)(Object)this).getStat().addStat(UhcGamePlayer.EnumStat.ENTITY_KILLED, 1);
	}

	/**
	 * TC Plugin: Kill entity hook
	 */
	@Redirect(
			method = "dropInventory",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V"
			)
	)
	private void dropInventoryWithoutClear(PlayerInventory playerInventory)
	{
		((IPlayerInventory)playerInventory).dropAllItemsWithoutClear();
	}
}
