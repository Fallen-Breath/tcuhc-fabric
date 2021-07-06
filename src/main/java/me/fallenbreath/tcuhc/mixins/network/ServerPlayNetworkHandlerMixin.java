package me.fallenbreath.tcuhc.mixins.network;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.helpers.ServerPlayerEntityHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin
{
	@Shadow public ServerPlayerEntity player;

	@Unique
	private boolean observe;

	@Unique
	private Entity entityToSpectate;

	@Inject(
			method = "onSpectatorTeleport",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void spectatorHook1(SpectatorTeleportC2SPacket packet, CallbackInfo ci, Iterator<?> var2, ServerWorld serverWorld, Entity entity)
	{
		Entity lastSpectateTarget = player.getCameraEntity();
		this.entityToSpectate = UhcGameManager.instance.onPlayerSpectate(player, entity, lastSpectateTarget);
		this.observe = UhcGameManager.instance.onPlayerSpectate(player, player, lastSpectateTarget) == player;

		ServerPlayerEntityHelper.doSpectateCheck.set(false);
		this.player.setCameraEntity(null);
		this.player.stopRiding();
	}

	@Redirect(
			method = "onSpectatorTeleport",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V"
			)
	)
	private void modifyTeleportTarget(ServerPlayerEntity playerEntity, ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch)
	{
		Entity entity = this.entityToSpectate;
		playerEntity.teleport(targetWorld, entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
	}

	@Inject(
			method = "onSpectatorTeleport",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V",
					shift = At.Shift.AFTER
			)
	)
	private void spectatorHook2(CallbackInfo ci)
	{
		if (!this.observe)
		{
			ServerPlayerEntityHelper.doSpectateCheck.set(false);
			this.player.setCameraEntity(this.observe ? null : this.entityToSpectate);
		}
	}

	@Redirect(
			method = "onChatMessage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Z)V"
			)
	)
	private void optioalChatting(PlayerManager playerManager, Text text, boolean system, /* parent method parameters -> */ ChatMessageC2SPacket packet)
	{
		// vanilla copy
		String string = packet.getChatMessage();
		string = StringUtils.normalizeSpace(string);
		// vanilla copy ends

		if (UhcGameManager.instance.onPlayerChat(player, string))
		{
			playerManager.broadcastChatMessage(text, system);
		}
	}
}