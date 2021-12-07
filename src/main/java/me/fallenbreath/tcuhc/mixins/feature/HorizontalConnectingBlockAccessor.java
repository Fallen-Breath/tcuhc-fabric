package me.fallenbreath.tcuhc.mixins.feature;

import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(HorizontalConnectingBlock.class)
public interface HorizontalConnectingBlockAccessor
{
	@Accessor
	static Map<Direction, BooleanProperty> getFACING_PROPERTIES()
	{
		throw new AbstractMethodError();
	}
}
