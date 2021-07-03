package me.fallenbreath.tcuhc.mixins.util;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SheepEntity.class)
public interface SheepEntityAccessor
{
	@Accessor
	static Map<DyeColor, ItemConvertible> getDROPS()
	{
		return null;
	}
}
