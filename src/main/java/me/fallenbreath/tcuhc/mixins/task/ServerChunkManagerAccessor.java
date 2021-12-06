package me.fallenbreath.tcuhc.mixins.task;

import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerChunkManager.class)
public interface ServerChunkManagerAccessor
{
	@Invoker
	ChunkHolder invokeGetChunkHolder(long pos);

	@SuppressWarnings("UnusedReturnValue")
	@Invoker
	boolean invokeTick();
}
