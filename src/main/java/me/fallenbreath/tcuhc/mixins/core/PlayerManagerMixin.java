package me.fallenbreath.tcuhc.mixins.core;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin
{
	@Inject(
			method = "onPlayerConnect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerWorld;onPlayerConnected(Lnet/minecraft/server/network/ServerPlayerEntity;)V"
			)
	)
	private void playerJoinHook(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci)
	{
		UhcGameManager.instance.onPlayerJoin(player);
	}

	@Inject(
			method = "respawnPlayer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;getLevelProperties()Lnet/minecraft/world/level/LevelProperties;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void playerRespawnHook(ServerPlayerEntity player, DimensionType dimension, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos blockPos, boolean bl, ServerPlayerInteractionManager serverPlayerInteractionManager2, ServerPlayerEntity serverPlayerEntity, ServerWorld serverWorld)
	{
		UhcGameManager.instance.onPlayerRespawn(serverPlayerEntity);
	}
}
