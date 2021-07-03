package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.util.SpawnPlatform;
import net.minecraft.server.network.ServerPlayerEntity;

public class TaskSpawnPlatformProtect extends Task {
	
	private UhcGameManager gameManager;
	
	public TaskSpawnPlatformProtect(UhcGameManager manager) {
		gameManager = manager;
	}
	
	@Override
	public void onUpdate() {
		for (ServerPlayerEntity player : gameManager.getServerPlayerManager().getPlayerList()) {
			if (player.isAlive() && !player.isCreative() && !player.isSpectator()) {
				if (player.getPos().getY() < SpawnPlatform.height - 20 && Math.abs(player.getPos().getX()) < 64 && Math.abs(player.getPos().getZ()) < 64) {
					gameManager.getUhcPlayerManager().randomSpawnPosition(player);
				}
			}
		}
	}
	
	@Override
	public boolean hasFinished() {
		return gameManager.isGamePlaying();
	}

}
