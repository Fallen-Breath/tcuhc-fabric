package me.fallenbreath.tcuhc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

	public static Identifier id(String name)
	{
		return new Identifier(MOD_ID, name);
	}

	public static Path getConfigPath()
	{
		return ConfigPathHolder.PATH;
	}

	private static class ConfigPathHolder
	{
		private static final Path PATH = create();

		public static Path create()
		{
			Path baseConfigDir = FabricLoader.getInstance().getConfigDir();
			Path modConfigDir = baseConfigDir.resolve(MOD_ID);
			if (!Files.exists(modConfigDir))
			{
				try
				{
					Files.createDirectories(modConfigDir);
				}
				catch (IOException e)
				{
					throw new RuntimeException("Creating config directory for " + MOD_ID, e);
				}
			}
			return modConfigDir;
		}
	}
}
