package me.fallenbreath.tcuhc.mixins.core;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin
{
	@Inject(
			method = "onPlayerConnect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/world/ServerWorld;method_18213(Lnet/minecraft/server/network/ServerPlayerEntity;)V"
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
			)
	)
	private void playerRespawnHook(ServerPlayerEntity player, DimensionType dimension, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir)
	{
		UhcGameManager.instance.onPlayerRespawn(player);
	}
}
