package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

	@Unique
	private static final double MAX_RANGE_SQR = 32 * 32;

	@Unique
	private int attackCooldown;

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

			double distanceSqrToTarget = this.target == null ? MAX_RANGE_SQR : this.target.squaredDistanceTo(this);
			if (this.target != null && distanceSqrToTarget < MAX_RANGE_SQR && this.target.canSee(this))  // has valid target
			{
				this.attackCooldown--;
				if (this.attackCooldown <= 0)
				{
					float amount;
					if (distanceSqrToTarget < 8 * 8)
					{
						amount = 2.0F;
						this.attackCooldown = 25;
					}
					else if (distanceSqrToTarget < 16 * 16)
					{
						amount = 1.5F;
						this.attackCooldown = 28;
					}
					else
					{
						amount = 1.0F;
						this.attackCooldown = 30;
					}
					this.target.damage(new EntityDamageSource("mob", this), amount);
				}
			}
			else  // no target
			{
				// reset
				this.target = null;
				this.attackCooldown = 40;

				if (this.age % 5 == 0)  // search target every 5gt
				{
					double maxDistance = MAX_RANGE_SQR;
					for (PlayerEntity player : this.world.getNonSpectatingEntities(PlayerEntity.class, this.getBoundingBox().expand(32, 10, 32)))
					{
						if (player.isCreative() || player.isSpectator())
						{
							continue;
						}

						double newdis = player.squaredDistanceTo(this);
						if (newdis < maxDistance && player.canSee(this))
						{
							maxDistance = newdis;
							this.target = player;
						}
					}
					if (this.target != null)  // target found
					{
						this.target.playSound(SoundEvents.ENTITY_GUARDIAN_ATTACK, SoundCategory.HOSTILE, 1.0F, 1.0F);
					}
				}
			}
		}
	}
}
