package me.fallenbreath.tcuhc.mixins.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
{
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/MinecraftServer;shouldSpawnNpcs()Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void checkMobDespawn(BooleanSupplier shouldKeepTicking, CallbackInfo ci, Profiler profiler, ObjectIterator<?> objectIterator, Int2ObjectMap.Entry<Entity> entry, Entity entity2, Entity entity3)
	{
		if (true)  // no need in 1.15+
		{
			return;
		}
		if (entity2 instanceof MobEntity)
		{
			profiler.push("checkDespawn");
			if (!entity2.removed)
			{
				((MobEntityAccessor)entity2).invokeCheckDespawn();
			}
			profiler.pop();
		}
	}
}
