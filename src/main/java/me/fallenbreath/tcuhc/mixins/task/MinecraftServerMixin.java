package me.fallenbreath.tcuhc.mixins.task;

import me.fallenbreath.tcuhc.task.TaskPregenerate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements TaskPregenerate.IMinecraftServer
{
	@Shadow private boolean field_19249;

	@Shadow private long field_19248;

	@Shadow private long timeReference;

	@Override
	public boolean hasTimeLeft()
	{
		return Util.getMeasuringTimeMs() < (this.field_19249 ? this.field_19248 : this.timeReference);
	}
}
