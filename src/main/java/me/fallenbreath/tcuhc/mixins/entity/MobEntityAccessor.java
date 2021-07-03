package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEntity.class)
public interface MobEntityAccessor
{
	@Invoker
	void invokeCheckDespawn();
}
