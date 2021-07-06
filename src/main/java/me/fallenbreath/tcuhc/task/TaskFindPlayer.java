/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.UhcGamePlayer;
import net.minecraft.server.network.ServerPlayerEntity;

public class TaskFindPlayer extends Task {
	
	private UhcGamePlayer player;
	private ServerPlayerEntity realPlayer;
	
	public TaskFindPlayer(UhcGamePlayer player) {
		this.player = player;
	}

	public UhcGamePlayer getGamePlayer() {
		return player;
	}

	public void onFindPlayer(ServerPlayerEntity player) { }
	
	@Override
	public final void onUpdate() {
		player.getRealPlayer().ifPresent(playermp -> {
			realPlayer = playermp;
			onFindPlayer(realPlayer);
		});
	}
	
	@Override
	public final boolean hasFinished() {
		return realPlayer != null;
	}

}
