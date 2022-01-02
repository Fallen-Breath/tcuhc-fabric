package me.fallenbreath.tcuhc.mixins.item;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin
{
	@Inject(method = "eatFood", at = @At("HEAD"))
	private void checkAndAddUhcGAppleStat(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir)
	{
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (self instanceof ServerPlayerEntity && stack.getItem() == Items.GOLDEN_APPLE)
		{
			float value;
			if (stack.getNbt() != null)
			{
				int goldenAppleLevel = stack.getNbt().getInt("level");
				value = goldenAppleLevel / 4.0F;
			}
			else
			{
				value = 1.0F;
			}
			UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(self).getStat().addStat(UhcGamePlayer.EnumStat.GOLDEN_APPLE_EATEN, value);
		}
	}
}
