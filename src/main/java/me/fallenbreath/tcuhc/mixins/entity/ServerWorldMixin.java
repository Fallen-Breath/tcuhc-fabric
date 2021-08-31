package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin
{
//	@Inject(
//			method = "tick",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/server/MinecraftServer;shouldSpawnNpcs()Z"
//			),
//			locals = LocalCapture.CAPTURE_FAILHARD
//	)
//	private void checkMobDespawn(BooleanSupplier shouldKeepTicking, CallbackInfo ci, Profiler profiler, ObjectIterator<?> objectIterator, Int2ObjectMap.Entry<Entity> entry, Entity entity2, Entity entity3)
//	{
//		if (true)  // no need in 1.15+
//		{
//			return;
//		}
//	}
}
