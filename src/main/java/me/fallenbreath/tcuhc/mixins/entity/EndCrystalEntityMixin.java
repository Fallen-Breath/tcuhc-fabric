package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalEntityMixin extends Entity
{
	@Unique
	private PlayerEntity target;

	@Shadow
	public abstract void setBeamTarget(@Nullable BlockPos blockPos);

	public EndCrystalEntityMixin(EntityType<?> type, World world)
	{
		super(type, world);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci)
	{
		if (!this.world.isClient())
		{
			this.setBeamTarget(this.target == null ? null : new BlockPos(this.target.getPos().getX(), this.target.getPos().getY() - 0.5, this.target.getPos().getZ()));

			if (this.age % 5 == 0)
			{
				double distanceSqr = this.target == null ? 1024 : this.target.squaredDistanceTo(this);
				if (this.target != null && distanceSqr < 1024 && this.target.canSee(this))
				{
					if (this.age % 30 == 0)
					{
						float amount;
						if (distanceSqr < 8 * 8) amount = 2.0F;
						else if (distanceSqr < 16 * 16) amount = 1.5F;
						else amount = 1.0F;
						this.target.damage(new EntityDamageSource("mob", this), amount);
					}
				}
				else
				{
					this.target = null;

					for (PlayerEntity player : this.world.getNonSpectatingEntities(PlayerEntity.class, this.getBoundingBox().expand(32, 10, 32)))
					{
						if (player.isCreative() || player.isSpectator())
						{
							continue;
						}

						double newdis = player.squaredDistanceTo(this);
						if (newdis < distanceSqr && player.canSee(this))
						{
							distanceSqr = newdis;
							this.target = player;
						}
					}
				}
			}
		}
	}
}
