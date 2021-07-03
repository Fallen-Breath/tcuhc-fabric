package me.fallenbreath.tcuhc.util;

import com.google.gson.Gson;
import me.fallenbreath.tcuhc.mixins.loot.LootManagerAccessor;
import net.minecraft.loot.LootPool;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class LootTableUtil
{
	public static LootPool getLeavesDropAppleLootPool()
	{
		Gson gson = LootManagerAccessor.getGSON();
		InputStream inputStream = LootTableUtil.class.getClassLoader().getResourceAsStream("assets/tc-uhc/apple.json");
		if (inputStream != null && gson != null)
		{
			Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			return JsonHelper.deserialize(gson, reader, LootPool.class);
		}
		else
		{
			throw new RuntimeException("Unable to load built-in loot table");
		}
	}
}
