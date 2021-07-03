package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.mixins.task.DyeColorAccessor;
import me.fallenbreath.tcuhc.task.Task.TaskTimer;
import me.fallenbreath.tcuhc.util.TitleUtil;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ByteTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.Collections;

public class TaskTitleCountDown extends TaskTimer {
	
	private int count;

	public TaskTitleCountDown(int init, int delay, int interval) {
		super(delay, interval);
		count = init;
	}
	
	@Override
	public void onTimer() {
		TitleUtil.sendTitleToAllPlayers(Formatting.GOLD + String.valueOf(--count), null);
		if (count == 0) this.setCanceled();
	}
	
	@Override
	public void onFinish() {
		TitleUtil.sendTitleToAllPlayers("Game Started !", "Enjoy Yourself !");
		UhcGameManager.instance.getUhcPlayerManager().getCombatPlayers().forEach(player -> player.addTask(new TaskFindPlayer(player) {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void onFindPlayer(ServerPlayerEntity player) {
				player.setGameMode(GameMode.SURVIVAL);
				player.setInvulnerable(false);
				player.clearStatusEffects();
				UhcGameManager.instance.getUhcPlayerManager().resetHealthAndFood(player);
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200, 4));  // 10s Resistance V

				// give invisibility and shiny potion to player for ghost mode
				switch (UhcGameManager.getGameMode()) {
					case GHOST:
						this.getGamePlayer().addGhostModeEffect();
						ItemStack shinyPotion = new ItemStack(Items.SPLASH_POTION).setCustomName(new LiteralText("Splash Shiny Potion"));
						PotionUtil.setCustomPotionEffects(shinyPotion, Collections.singleton(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0)));
						player.inventory.insertStack(shinyPotion);
						break;
					case KING:
						if (this.getGamePlayer().isKing()) {
							DyeColor dyeColor = this.getGamePlayer().getTeam().getTeamColor().dyeColor;
							ItemStack kingsHelmet = new ItemStack(Items.LEATHER_HELMET).setCustomName(new LiteralText(String.format("%s crown", dyeColor.getName())));
							kingsHelmet.getOrCreateTag().put("KingsCrown", new ByteTag((byte)1));
							kingsHelmet.getOrCreateTag().put("Unbreakable", new ByteTag((byte)1));
							((DyeableItem)Items.LEATHER_HELMET).setColor(kingsHelmet, ((DyeColorAccessor)(Object)dyeColor).getColor());
							kingsHelmet.addEnchantment(Enchantments.PROTECTION, 6);
							kingsHelmet.addEnchantment(Enchantments.BINDING_CURSE, 1);
							kingsHelmet.addEnchantment(Enchantments.VANISHING_CURSE, 1);
							player.equipStack(EquipmentSlot.HEAD, kingsHelmet);
						}
						break;
				}
			}
		}));
		if (UhcGameManager.getGameMode() == UhcGameManager.EnumMode.KING) {
			UhcGameManager.instance.addTask(new TaskKingEffectField());
		}
		UhcGameManager.instance.addTask(new TaskScoreboard());
	}

}
