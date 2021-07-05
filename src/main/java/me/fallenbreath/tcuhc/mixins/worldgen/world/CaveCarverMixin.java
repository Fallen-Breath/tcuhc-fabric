package me.fallenbreath.tcuhc.mixins.worldgen.world;

import net.minecraft.world.gen.carver.CaveCarver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(CaveCarver.class)
public abstract class CaveCarverMixin
{
	@Shadow protected abstract int getMaxCaveCount();

	@Redirect(
			method = "carve",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Random;nextInt(I)I",
					ordinal = 2
			)
	)
	private int customRandomizer(Random random, int bound)
	{
		int customBound = random.nextInt(this.getMaxCaveCount());
		if (random.nextInt(2) == 0)
		{
			customBound = random.nextInt(customBound + 1);
		}
		return customBound;
	}
}
