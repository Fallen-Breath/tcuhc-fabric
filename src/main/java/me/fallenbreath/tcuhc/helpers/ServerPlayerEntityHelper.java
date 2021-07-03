package me.fallenbreath.tcuhc.helpers;

public class ServerPlayerEntityHelper
{
	public static final ThreadLocal<Boolean> doSpectateCheck = ThreadLocal.withInitial(() -> true);
}
