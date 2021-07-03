package me.fallenbreath.tcuhc.mixins.loot;

import com.google.gson.Gson;
import net.minecraft.loot.LootManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootManager.class)
public interface LootManagerAccessor
{
	@Accessor
	static Gson getGSON()
	{
		return null;
	}
}
