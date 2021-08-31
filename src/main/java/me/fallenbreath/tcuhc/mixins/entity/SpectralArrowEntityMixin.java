package me.fallenbreath.tcuhc.mixins.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpectralArrowEntity.class)
public abstract class SpectralArrowEntityMixin extends PersistentProjectileEntity
{
	protected SpectralArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world)
	{
		super(entityType, world);
	}

	@Inject(method = "onHit", at = @At("TAIL"))
	private void onKill(LivingEntity target, CallbackInfo ci)
	{
		this.explode();
	}

	@Intrinsic
	@Override
	protected void onCollision(HitResult hitResult)
	{
		if (hitResult.getType() == HitResult.Type.BLOCK && this.isCritical())
		{
			this.updatePosition(hitResult.getPos().getX(), hitResult.getPos().getY(), hitResult.getPos().getZ());
			this.explode();
		}

		if (!this.isRemoved())
		{
			super.onCollision(hitResult);
		}
	}

	@Unique
	private float getExplosionResistance(World world, BlockPos blockPos)
	{
		BlockState blockState = this.world.getBlockState(blockPos);
		FluidState fluidState = this.world.getFluidState(blockPos);
		return Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance());
	}

	@Unique
	private void explode()
	{
		if (!this.isCritical() || this.isRemoved())
		{
			return;
		}
		this.discard();
		this.world.createExplosion(this, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), 1.0F, Explosion.DestructionType.DESTROY);
		BlockPos arrowpos = new BlockPos(this.getPos());
		if (getExplosionResistance(world, arrowpos) > 6.01f)
		{
			return;
		}
		for (int x = -1; x <= 1; x++)
		{
			for (int y = -2; y <= 1; y++)
			{
				for (int z = -1; z <= 1; z++)
				{
					BlockPos pos = arrowpos.add(x, y, z);
					if (getExplosionResistance(world, pos) < 6.01f)
					{
						this.world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
				}
			}
		}
	}
}
