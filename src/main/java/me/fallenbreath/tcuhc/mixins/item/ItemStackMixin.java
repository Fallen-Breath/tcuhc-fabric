package me.fallenbreath.tcuhc.mixins.item;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.util.PlayerItems;
import me.fallenbreath.tcuhc.util.Position;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
		if (miner instanceof ServerPlayerEntity)
		{
			if (state.getBlock() == Blocks.DIAMOND_ORE || state.getBlock() == Blocks.DEEPSLATE_DIAMOND_ORE)
			{
				UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(miner).getStat().addStat(UhcGamePlayer.EnumStat.DIAMOND_FOUND, 1);
			}
		}
	}

	@Inject(
			method = "useOnBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
			),
			cancellable = true
	)
	private void moralRespawnPlayerLogic(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
	{
		ItemStack self = (ItemStack)(Object)this;
		if (context.getWorld() instanceof ServerWorld && PlayerItems.isMoralItem(self))
		{
			BlockPos blockPos = context.getBlockPos();
			BlockState blockState = context.getWorld().getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.WITHER_SKELETON_SKULL || blockState.getBlock() == Blocks.WITHER_SKELETON_WALL_SKULL)
			{
				String playerName = PlayerItems.getMoralOwner(self);
				Vec3d vec3d = Vec3d.ofBottomCenter(blockPos);
				Position respawnPos = new Position(vec3d, context.getWorld().getRegistryKey(), 0.0F, 0.0F);
				if (UhcGameManager.instance.getUhcPlayerManager().resurrectPlayerUsingMoral(playerName, respawnPos))
				{
					self.decrement(1);
					context.getWorld().removeBlock(blockPos, false);
					context.getWorld().playSound(null, vec3d.getX(), vec3d.getY(), vec3d.getZ(), SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
					cir.setReturnValue(ActionResult.CONSUME);
				}
				else
				{
					PlayerEntity player = context.getPlayer();
					if (player != null)
					{
						player.sendMessage(Text.of(Formatting.RED + "You cannot resurrect " + playerName + " now."), false);
					}
				}
			}
		}
	}
}
