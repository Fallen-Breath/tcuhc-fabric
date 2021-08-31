package me.fallenbreath.tcuhc.mixins.access;

import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.nio.file.Path;

@Mixin(LevelStorage.Session.class)
public interface SessionAccessor
{
	@Accessor
	Path getDirectory();
}
