package me.fallenbreath.tcuhc.util;

import com.google.gson.Gson;
import me.fallenbreath.tcuhc.mixins.loot.LootManagerAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class LootTableUtil
{
	public static <T> T getUhcLootData(String type, String dir, Class<T> class_)
	{
		Gson gson = LootManagerAccessor.getGSON();
		String filePath = String.format("data/tcuhc/%s/%s.json", type, dir);
		InputStream inputStream = LootTableUtil.class.getClassLoader().getResourceAsStream(filePath);
		if (inputStream != null && gson != null)
		{
			Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			return JsonHelper.deserialize(gson, reader, class_);
		}
		else
		{
			throw new RuntimeException("Unable to load loot data from " + filePath);
		}
	}

	public static LootPool getUhcLootPool(String name)
	{
		return getUhcLootData("lootpools", name, LootPool.class);
	}

	public static LootEntry getUhcLootEntry(String name)
	{
		return getUhcLootData("lootentries", name, LootEntry.class);
	}
}
