package me.fallenbreath.tcuhc.mixins.core;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
	@Shadow public abstract String getLevelName();

	@Shadow @Final private File gameDir;

	private UhcGameManager uhcGameManager;
	private boolean serverInited = false;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void constructUhcGameManager(CallbackInfo ci)
	{
		this.uhcGameManager = new UhcGameManager((MinecraftServer)(Object)this);
		UhcGameManager.tryUpdateSaveFolder(this.gameDir.toPath().resolve(this.getLevelName()));
	}

	@Inject(method = "loadWorld", at = @At("HEAD"))
	private void setSpawnPosTo00(CallbackInfo ci)
	{
		ServerWorld world = this.uhcGameManager.getOverWorld();
		world.setSpawnPos(BlockPos.ORIGIN.up(world.getChunkManager().getChunkGenerator().getSpawnHeight()));
	}

	@Inject(
			method = "run",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/MinecraftServer;setFavicon(Lnet/minecraft/server/ServerMetadata;)V"
			)
	)
	private void postInitUhcGameManager(CallbackInfo ci)
	{
		this.uhcGameManager.onServerInited();
		this.serverInited = true;
	}

	@Inject(method = "tick", at = @At(value = "HEAD"))
	private void tickDurationSamplingStart(CallbackInfo ci)
	{
		this.uhcGameManager.msptRecorder.startTick();
	}

	@Inject(
			method = "method_16208",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/MinecraftServer;runTasks(Ljava/util/function/BooleanSupplier;)V"
			)
	)
	private void tickDurationSamplingEnd(CallbackInfo ci)
	{
		if (this.serverInited)
		{
			this.uhcGameManager.msptRecorder.endTick();
		}
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=tallying"
			)
	)
	private void tickUhcGameManager(CallbackInfo ci)
	{
		this.uhcGameManager.tick();
	}
}
