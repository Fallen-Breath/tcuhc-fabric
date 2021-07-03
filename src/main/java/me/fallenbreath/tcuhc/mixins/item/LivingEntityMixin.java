package me.fallenbreath.tcuhc.mixins.item;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
{
	@Unique
	private int goldenAppleLevel;

	@Inject(
			method = "applyFoodEffects",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
			)
	)
	private void modifyEffects(ItemStack stack, World world, LivingEntity targetEntity, CallbackInfo ci)
	{
		if (stack.getItem() == Items.GOLDEN_APPLE && stack.getTag() != null)
		{
			this.goldenAppleLevel = stack.getTag().getInt("level");
		}
		else
		{
			this.goldenAppleLevel = 0;
		}
	}

	@SuppressWarnings("ConstantConditions")
	@Redirect(
			method = "applyFoodEffects",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
			)
	)
	private Iterator<Pair<StatusEffectInstance, Float>> modifyIterator(List<Pair<StatusEffectInstance, Float>> list)
	{
		int level = this.goldenAppleLevel;
		if (level > 0)
		{
			List<Pair<StatusEffectInstance, Float>> newList = Lists.newArrayList(list);
			for (int i = 0; i < newList.size(); i++)
			{
				StatusEffectInstance effect = newList.get(i).getLeft();
				float chance = newList.get(i).getRight();
				StatusEffectInstance newEffect = new StatusEffectInstance(effect);
				StatusEffect effectType = newEffect.getEffectType();
				if (effectType == StatusEffects.REGENERATION)
				{
					((StatusEffectInstanceAccessor)newEffect).setDuration((level + 1) * 20);
				}
				else if (effectType == StatusEffects.ABSORPTION)
				{
					((StatusEffectInstanceAccessor)newEffect).setAmplifier(level - 1);
				}
				newList.set(i, Pair.of(newEffect, chance));
			}
			return newList.iterator();
		}
		return list.iterator();
	}
}
