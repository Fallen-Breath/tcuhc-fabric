package me.fallenbreath.tcuhc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;

public class TcUhcMod implements ModInitializer
{
	private static final String MOD_ID = "tcuhc";
	private static String version;
	private static final String MINECRAFT_VERSION = MinecraftVersion.CURRENT.getName();

	@Override
	public void onInitialize()
	{
		version = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
	}

	public static String getModId()
	{
		return MOD_ID;
	}

	public static String getModVersion()
	{
		return version;
	}

	public static String getMinecraftVersion()
	{
		return MINECRAFT_VERSION;
	}
}
