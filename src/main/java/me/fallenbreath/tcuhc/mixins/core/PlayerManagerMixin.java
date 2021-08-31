package me.fallenbreath.tcuhc.mixins.core;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

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
					target = "Lnet/minecraft/world/World;getLevelProperties()Lnet/minecraft/world/WorldProperties;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void playerRespawnHook(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld, Optional optional2, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity, boolean bl2)
	{
		UhcGameManager.instance.onPlayerRespawn(serverPlayerEntity);
	}
}
