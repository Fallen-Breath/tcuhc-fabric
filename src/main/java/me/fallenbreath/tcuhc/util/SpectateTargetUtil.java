/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.UhcGameTeam;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class SpectateTargetUtil {
	
	public static boolean isCapableTarget(UhcGamePlayer player, Entity target) {
		UhcGameManager gameManager = UhcGameManager.instance;
		if (!gameManager.isGamePlaying()) return true;
		if (gameManager.getUhcPlayerManager().isObserver(player)) return true;
		if (!gameManager.getOptions().getBooleanOptionValue("forceViewport")) return true;
		UhcGameTeam team = player.getTeam();
		if (team.getAliveCount() == 0) return true;
		if (target instanceof ArmorStandEntity) return true;
		if (!(target instanceof ServerPlayerEntity)) return false;
		if (UhcGameManager.instance.getServerPlayerManager().getPlayer(target.getUuid()) != target) return false;
		UhcGamePlayer targetPlayer = gameManager.getUhcPlayerManager().getGamePlayer((ServerPlayerEntity) target);
		if (player == targetPlayer) return false;
		return player.getTeam() == targetPlayer.getTeam();
	}
	
	public static Entity getCapableTarget(UhcGamePlayer player, Entity origin) {
		if (isCapableTarget(player, origin)) return origin;
		for (UhcGamePlayer target : player.getTeam().getPlayers()) {
			if (target.isAlive() && target.getRealPlayer().isPresent()) {
				if (origin instanceof ArmorStandEntity) origin.remove();
				return target.getRealPlayer().get();
			}
		}
		for (UhcGamePlayer target : player.getTeam().getPlayers()) {
			if (target.isAlive()) {
				ServerPlayerEntity playermp = player.getRealPlayer().get();
				Vec3d pos = playermp.getPos();
				ArmorStandEntity armorStand = new ArmorStandEntity(playermp.world, pos.x, pos.y, pos.z);
				armorStand.setInvisible(true);
				armorStand.setInvulnerable(true);
				armorStand.setNoGravity(true);
				playermp.world.spawnEntity(armorStand);
				return armorStand;
			}
		}
		return origin;
	}

}
