/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc;

import com.google.common.collect.Maps;
import me.fallenbreath.tcuhc.options.Options;
import me.fallenbreath.tcuhc.task.Taskable;
import me.fallenbreath.tcuhc.util.Position;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class UhcGamePlayer extends Taskable {
	
	private static final UhcPlayerManager playerManager = UhcGameManager.instance.getUhcPlayerManager();
	
	private final UUID playerUUID;
	private final String playerName;
	
	protected boolean isAlive;
	private UhcGameTeam team;
	@Nullable
	private UhcGameColor colorSelected = null;
	
	protected int deathTime;
	@Nullable
	private Position deathPos = null;
	private final PlayerStatistics statistics = new PlayerStatistics();
	
	private int borderReminder;
	
	public UhcGamePlayer(ServerPlayerEntity realPlayer) {
		playerUUID = realPlayer.getUuid();
		playerName = realPlayer.getEntityName();
		isAlive = true;
	}
	
	public UhcGameTeam getTeam() { return team; }
	protected void setTeam(UhcGameTeam team) { this.team = team; }
	public int getDeathTime() { return deathTime; }
	@Nullable
	public Position getDeathPos() { return deathPos; }
	public void resetDeathPos() { this.deathPos = null; }
	public void setColorSelected(@Nullable UhcGameColor color) { colorSelected = color; }
	public Optional<UhcGameColor> getColorSelected() { return Optional.ofNullable(colorSelected); }
	public String getName() { return playerName; }
	public boolean isAlive() { return isAlive; }
	public PlayerStatistics getStat() { return statistics; }
	
	public boolean isSamePlayer(PlayerEntity player) {
		return player != null && playerUUID.equals(player.getUuid());
	}

	public boolean isKing() {
		return this.getTeam() != null && this.getTeam().getKing() == this;
	}
	
	public void setDead(int curTime) {
		if (isAlive) {
			isAlive = false;
			deathTime = curTime;
			deathPos = getRealPlayer().map(player -> new Position(player.getPos(), player.getWorld().getRegistryKey(), player.getYaw(), player.getPitch())).orElse(null);
			statistics.setStat(EnumStat.ALIVE_TIME, Options.instance.getIntegerOptionValue("gameTime") - deathTime);
		}
	}
	
	public void tick() {
		this.updateTasks();
		if (borderReminder > 0) borderReminder--;
	}
	
	public boolean borderRemindCooldown() {
		if (borderReminder == 0) {
			borderReminder = 200;
			return true;
		} else return false;
	}
	
	public Optional<ServerPlayerEntity> getRealPlayer() {
		return playerManager.getPlayerByUUID(playerUUID);
	}

	public void addGhostModeEffect() {
		this.getRealPlayer().ifPresent(player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 0, true, false)));
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public enum EnumStat {
		PLAYER_KILLED("Player Killed"),
		ENTITY_KILLED("Entity Killed"),
		DAMAGE_TAKEN("Damage Taken"),
		DAMAGE_BLOCKED("Damage Blocked"),
		DAMAGE_DEALT("Damage Dealt"),
		DAMAGE_BEING_BLOCKED("Damage Being Blocked"),
		FRIENDLY_FIRE("Friendly Fire"),
		CHEST_FOUND("Chest Found"),
		EMPTY_CHEST_FOUND("Empty Chest Found"),
		DIAMOND_FOUND("Diamond Found"),
		HEALTH_HEALED("Health Healed"),
		GOLDEN_APPLE_EATEN("Golden Apple Eaten"),
		ALIVE_TIME("Alive Time");
		
		public final String name;
		
		EnumStat(String name) {
			this.name = name;
		}
	}
	
	public static class PlayerStatistics {
		
		private final Map<EnumStat, Float> stats = Maps.newEnumMap(EnumStat.class);
		
		public PlayerStatistics() {
			clear();
		}
		
		public void clear() {
			for (EnumStat stat : EnumStat.values()) {
				stats.put(stat, 0.0f);
			}
		}
		
		public void addStat(EnumStat stat, float value) {
			if (UhcGameManager.instance.isGamePlaying())
				stats.put(stat, stats.get(stat) + value);
		}

		public void setStat(EnumStat stat, float value) {
			if (UhcGameManager.instance.isGamePlaying())
				stats.put(stat, value);
		}
		
		public float getFloatStat(EnumStat stat) {
			return stats.get(stat);
		}
	}

}
