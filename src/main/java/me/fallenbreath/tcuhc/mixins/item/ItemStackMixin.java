package me.fallenbreath.tcuhc.mixins.item;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	@Inject(
			method = "postMine",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V"
			)
	)
	private void onPlayerMinedBlock(World world, BlockState state, BlockPos pos, PlayerEntity miner, CallbackInfo ci)
	{
		if (miner instanceof ServerPlayerEntity && state.getBlock() == Blocks.DIAMOND_ORE)
		{
			UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(miner).getStat().addStat(UhcGamePlayer.EnumStat.DIAMOND_FOUND, 1);
		}
	}
}
