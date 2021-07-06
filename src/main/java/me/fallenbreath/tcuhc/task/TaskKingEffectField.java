/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class TaskKingEffectField extends Task.TaskTimer
{
	public TaskKingEffectField()
	{
		super(0, 10);
	}

	// give 2 seconds speed I to any player near its king
	@Override
	public void onTimer()
	{
		if (UhcGameManager.instance.isGamePlaying() && UhcGameManager.getGameMode() == UhcGameManager.EnumMode.KING) {
			UhcGameManager.instance.getUhcPlayerManager().getCombatPlayers().forEach(gamePlayer -> {
				// only non-king players are able to have the boost
				if (!gamePlayer.isKing() && gamePlayer.isAlive() && gamePlayer.getRealPlayer().isPresent()) {
					UhcGamePlayer king = gamePlayer.getTeam().getKing();
					if (king.isAlive() && king.getRealPlayer().isPresent()) {
						double distanceToKing = gamePlayer.getRealPlayer().get().distanceTo(king.getRealPlayer().get());
						if (distanceToKing <= 5) {
							gamePlayer.getRealPlayer().get().addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 40, 0));
						}
					}
				}
			});
		}
		else {
			this.setCanceled();
		}
	}
}
