package me.fallenbreath.tcuhc.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fallenbreath.tcuhc.UhcGameManager;

import java.io.*;

public class UhcWorldData
{
	public int spawnPlatformHeight = -1;

	public boolean isSpawnPlatformHeightValid()
	{
		return this.spawnPlatformHeight != -1;
	}

	public static UhcWorldData load()
	{
		File file = UhcGameManager.getDataFile();
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file)))
		{
			return new Gson().fromJson(reader, UhcWorldData.class);
		}
		catch (Exception e)
		{
			if (!(e instanceof FileNotFoundException))
			{
				UhcGameManager.LOG.error("Failed to read uhc data file", e);
			}
			return new UhcWorldData();
		}
	}

	public void save()
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
}
