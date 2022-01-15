package me.fallenbreath.tcuhc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fallenbreath.tcuhc.UhcGameManager;

import java.io.*;
import java.util.Random;

public class UhcWorldData
{
	public int spawnPlatformHeight = -1;
	public StructureType netherFortressType = StructureType.randomChoose();

	private UhcWorldData()
	{
		this.save();
	}

	public boolean isSpawnPlatformHeightValid()
	{
		return this.spawnPlatformHeight != -1;
	}

	public static UhcWorldData load()
	{
		File file = UhcGameManager.getDataFile();
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file)))
		{
			UhcWorldData data = new Gson().fromJson(reader, UhcWorldData.class);
			UhcGameManager.LOG.info("Loaded uhc world data");
			return data;
		}
		catch (Exception e)
		{
			if (e instanceof FileNotFoundException)
			{
				UhcGameManager.LOG.warn("Generating new uhc world data");
			}
			else
			{
				UhcGameManager.LOG.error("Failed to read uhc world data file", e);
			}
			return new UhcWorldData();
		}
	}

	public synchronized void save()
	{
		File file = UhcGameManager.getDataFile();
		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file)))
		{
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this));
		}
		catch (Exception e)
		{
			UhcGameManager.LOG.error("Failed to save uhc data file", e);
		}
	}

	public enum StructureType
	{
		NETHER_FORTRESS,
		BASTION_REMNANT;

		private static final Random random = new Random();

		public static StructureType randomChoose()
		{
			StructureType[] values = values();
			return values[random.nextInt(values.length)];
		}
	}
}
