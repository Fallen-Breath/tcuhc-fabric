package me.fallenbreath.tcuhc.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class Position
{
	public final Vec3d pos;
	public final RegistryKey<World> dimension;
	public final float yaw;
	public final float pitch;

	public Position(Vec3d pos, RegistryKey<World> dimension, float yaw, float pitch)
	{
		this.pos = pos;
		this.dimension = dimension;
		this.yaw = yaw;
		this.pitch = pitch;
	}
}
