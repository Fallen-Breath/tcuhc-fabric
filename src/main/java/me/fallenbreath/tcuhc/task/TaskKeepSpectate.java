package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.task.Task.TaskTimer;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;

public class TaskKeepSpectate extends TaskTimer {
	
	private UhcGamePlayer gamePlayer;
	
	public TaskKeepSpectate(UhcGamePlayer gamePlayer) {
		super(0, 10);
		this.gamePlayer = gamePlayer;
	}
	
	@Override
	public void onTimer() {
		if (!UhcGameManager.instance.isGamePlaying() || gamePlayer.getTeam().getAliveCount() == 0 || gamePlayer.isAlive())
		{
			this.setCanceled();
			return;
		}
		gamePlayer.getRealPlayer().ifPresent(player -> {
			if (player.isSpectator())
			{
				Entity target = player.getCameraEntity();
				if (target != null)
				{
					player.setCameraEntity(target);
					if (player.world != target.world)
					{
						player.networkHandler.onSpectatorTeleport(new SpectatorTeleportC2SPacket(target.getUuid()));
					}
				}
			}
		});
	}
	
	@Override
	public void onFinish() {
		gamePlayer.getRealPlayer().ifPresent(player -> player.setCameraEntity(player));
	}

}
