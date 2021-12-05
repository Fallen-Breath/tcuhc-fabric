package me.fallenbreath.tcuhc.mixins.core;

import me.fallenbreath.tcuhc.UhcGameManager;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelStorage.class)
public abstract class LevelStorageMixin
{
	@Inject(method = "createSession", at = @At("TAIL"))
	private void removeOldUhcWorld(String directoryName, CallbackInfoReturnable<LevelStorage.Session> cir)
	{
		UhcGameManager.tryUpdateSaveFolder(cir.getReturnValue().getDirectory(WorldSavePath.ROOT));
	}
}
