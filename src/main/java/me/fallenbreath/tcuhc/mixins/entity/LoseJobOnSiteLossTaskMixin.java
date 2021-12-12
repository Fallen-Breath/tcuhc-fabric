package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.ai.brain.task.LoseJobOnSiteLossTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LoseJobOnSiteLossTask.class)
public abstract class LoseJobOnSiteLossTaskMixin
{
	@Inject(method = "shouldRun", at = @At("HEAD"), cancellable = true)
	private void villagerNeverLossesItsJob(CallbackInfoReturnable<Boolean> cir)
	{
		cir.setReturnValue(false);
	}
}
