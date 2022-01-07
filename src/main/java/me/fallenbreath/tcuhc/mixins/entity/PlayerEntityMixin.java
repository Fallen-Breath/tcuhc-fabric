package me.fallenbreath.tcuhc.mixins.entity;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.interfaces.IPlayerInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
	private float modifiedDamageAmount;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
	{
		super(entityType, world);
	}

	/**
	 * Added tag check for KING mode's king's crown item
	 */
	@ModifyArg(
			method = "vanishCursedItems",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/enchantment/EnchantmentHelper;hasVanishingCurse(Lnet/minecraft/item/ItemStack;)Z"
			),
			index = 0
	)
	private ItemStack kingsCrownWillNeverVanish(ItemStack itemStack)
	{
		if (itemStack.getNbt() != null && itemStack.getNbt().contains("KingsCrown"))
		{
			return ItemStack.EMPTY;
		}
		return itemStack;
	}

	/**
	 * TC Plugin: Kill entity hook
	 */
	@Inject(method = "onKilledOther", at = @At("TAIL"))
	private void onKill(ServerWorld serverWorld, LivingEntity livingEntity, CallbackInfo ci)
	{
		UhcGameManager.instance.getUhcPlayerManager().getGamePlayer((PlayerEntity)(Object)this).getStat().addStat(UhcGamePlayer.EnumStat.ENTITY_KILLED, 1);
	}

	/**
	 * TC Plugin: Kill entity hook
	 */
	@Redirect(
			method = "dropInventory",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V"
			)
	)
	private void dropInventoryWithoutClear(PlayerInventory playerInventory)
	{
		((IPlayerInventory)playerInventory).dropAllItemsWithoutClear();
	}

	@Inject(
			method = "damage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
			)
	)
	private void modifyAndRecordDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		this.modifiedDamageAmount = amount = UhcGameManager.instance.modifyPlayerDamage(amount);

		PlayerEntity self = (PlayerEntity)(Object)this;
		Entity sourceEntity = source.getSource();
		if (!(sourceEntity instanceof ServerPlayerEntity)) sourceEntity = source.getAttacker();
		if (sourceEntity instanceof ServerPlayerEntity && amount > 0.0F) {

			// target player
			UhcGamePlayer.EnumStat stat = UhcGamePlayer.EnumStat.DAMAGE_TAKEN;
			// the same logic in net.minecraft.entity.LivingEntity.damage
			if (this.blockedByShield(source)) {
				stat = UhcGamePlayer.EnumStat.DAMAGE_BLOCKED;
			}
			UhcGameManager.instance.getUhcPlayerManager().getGamePlayer(self).getStat().addStat(stat, amount);

			// source player
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
					target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
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
		PlayerEntity self = (PlayerEntity)(Object)this;
		if (cir.getReturnValue() && self instanceof ServerPlayerEntity)
		{
			UhcGameManager.instance.onPlayerDamaged((ServerPlayerEntity)self, source, this.modifiedDamageAmount);
		}
	}
}
