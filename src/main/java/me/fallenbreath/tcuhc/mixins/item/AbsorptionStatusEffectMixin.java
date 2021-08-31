package me.fallenbreath.tcuhc.mixins.item;

import net.minecraft.entity.effect.AbsorptionStatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbsorptionStatusEffect.class)
public abstract class AbsorptionStatusEffectMixin
{
	@ModifyConstant(method = {"onRemoved", "onApplied"}, constant = @Constant(intValue = 4))
	private int modifyBaseAbsorption(int value)
	{
		return 1;
	}
}
