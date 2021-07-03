package me.fallenbreath.tcuhc.mixins.block;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.gen.BonusChestFeature;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends LootableContainerBlockEntity
{
	protected ChestBlockEntityMixin(BlockEntityType<?> blockEntityType)
	{
		super(blockEntityType);
	}

	@Inject(method = "onInvOpen", at = @At("HEAD"))
	private void playerOpenChestHook(PlayerEntity player, CallbackInfo ci)
	{
		if (!player.isCreative() && !player.isSpectator() && this.getCustomName() != null) {
			UhcGamePlayer.EnumStat stat;
			switch (this.getCustomName().getString()) {
				case BonusChestFeature.BONUS_CHEST_NAME:
					stat = UhcGamePlayer.EnumStat.CHEST_FOUND;
					break;
				case BonusChestFeature.EMPTY_CHEST_NAME:
					stat = UhcGamePlayer.EnumStat.EMPTY_CHEST_FOUND;
					break;
				default:
					stat = null;
			}
			if (stat != null) {
				UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(player).getStat().addStat(stat, 1);
			}
		}
	}

	@Inject(method = "onInvClose", at = @At("HEAD"))
	private void playerCloseChestHook(PlayerEntity player, CallbackInfo ci)
	{
		if (!player.isCreative() && !player.isSpectator() && this.getCustomName() != null)
		{
			String customName = this.getCustomName().getString();
			if (customName.equals(BonusChestFeature.BONUS_CHEST_NAME) || customName.equals(BonusChestFeature.EMPTY_CHEST_NAME))
			{
				this.setCustomName(new LiteralText("Opened " + customName));
			}
		}
	}
}
