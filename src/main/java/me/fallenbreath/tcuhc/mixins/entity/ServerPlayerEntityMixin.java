package me.fallenbreath.tcuhc.mixins.entity;

import com.mojang.authlib.GameProfile;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.fallenbreath.tcuhc.helpers.ServerPlayerEntityHelper.doSpectateCheck;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity
{
	@Shadow private int field_13998;

	@Shadow private Entity cameraEntity;

	@Shadow public abstract boolean isSpectator();

	private Entity previousCameraEntity;
	private float modifiedDamageAmount;

	public ServerPlayerEntityMixin(World world, GameProfile profile)
	{
		super(world, profile);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void modifyInvulnerableDuration( CallbackInfo ci)
	{
		// TC Plugin: modified invulnerable time from 60gt to 20gt
		this.field_13998 = 20;
	}

	@Inject(
			method = "onDeath",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/stat/Stats;DEATHS:Lnet/minecraft/util/Identifier;"
			)
	)
	private void onPlayerDeath(DamageSource cause, CallbackInfo ci)
	{
		Entity sourceEntity = cause.getSource();
		if (!(sourceEntity instanceof PlayerEntity)) sourceEntity = cause.getAttacker();
		if (!(sourceEntity instanceof PlayerEntity)) sourceEntity = this.getAttacker();
		if (sourceEntity instanceof PlayerEntity)
		{
			UhcGameManager.instance.getUhcPlayerManager().getGamePlayer((PlayerEntity)sourceEntity).getStat().addStat(UhcGamePlayer.EnumStat.PLAYER_KILLED, 1);
		}
		// TC Plugin: Player Death Hook
		UhcGameManager.instance.onPlayerDeath((ServerPlayerEntity) (Object) this, cause);
	}

	@Inject(
			method = "damage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
			)
	)
	private void modifyAndRecordDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		this.modifiedDamageAmount = amount = UhcGameManager.instance.modifyPlayerDamage(amount);

		Entity sourceEntity = source.getSource();
		if (!(sourceEntity instanceof ServerPlayerEntity)) sourceEntity = source.getAttacker();
		if (sourceEntity instanceof ServerPlayerEntity) {
			UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(this).getStat().addStat(UhcGamePlayer.EnumStat.DAMAGE_TAKEN, amount);
			UhcGamePlayer.PlayerStatistics statistics = UhcGameManager.instance.getUhcPlayerManager().getGamePlayer((ServerPlayerEntity)sourceEntity).getStat();
			statistics.addStat(UhcGamePlayer.EnumStat.DAMAGE_DEALT, amount);
			if (this.getScoreboardTeam() != null && this.getScoreboardTeam().isEqual(sourceEntity.getScoreboardTeam())) {
				statistics.addStat(UhcGamePlayer.EnumStat.FRIENDLY_FIRE, amount);
			}
		}
	}

	@ModifyArg(
			method = "damage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
			),
			index = 1
	)
	private float modifyAndRecordDamage(float amount)
	{
		return this.modifiedDamageAmount;
	}

	@Inject(method = "damage", at = @At("RETURN"))
	private void afterDamageCalc(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		if (cir.getReturnValue())
		{
			UhcGameManager.instance.onPlayerDamaged((ServerPlayerEntity) (Object) this, source, this.modifiedDamageAmount);
		}
	}

	@ModifyVariable(method = "setCameraEntity", at = @At("STORE"), ordinal = 1)
	private Entity returnNullToAlwaysEnterIf(Entity entity2)
	{
		this.previousCameraEntity = entity2;
		return null;
	}

	@Inject(
			method = "setCameraEntity",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/server/network/ServerPlayerEntity;cameraEntity:Lnet/minecraft/entity/Entity;",
					ordinal = 1
			)
	)
	private void modifyCameraEntity(CallbackInfo ci)
	{
		if (doSpectateCheck.get() && this.isSpectator())
		{
			this.cameraEntity = UhcGameManager.instance.onPlayerSpectate((ServerPlayerEntity) (Object) this, this.cameraEntity, this.previousCameraEntity);
		}
		// Regular calls are always with it true
		doSpectateCheck.set(true);
	}

	@Intrinsic
	@Override
	public void heal(float healAmount)
	{
		float health = this.getHealth();
		super.heal(healAmount);
		UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(this).getStat().addStat(UhcGamePlayer.EnumStat.HEALTH_HEALED, getHealth() - health);
	}
}
