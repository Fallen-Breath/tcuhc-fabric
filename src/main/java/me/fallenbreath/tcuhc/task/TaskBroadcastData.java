/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.UhcGamePlayer.EnumStat;
import me.fallenbreath.tcuhc.task.Task.TaskTimer;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class TaskBroadcastData extends TaskTimer {
	private int round = 0;
	
	public TaskBroadcastData(int delay)  {
		super(delay, 20);
	}
	
	private String getGraph(int len) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < len; i++)
			res.append(i % 2 == 0 ? "[" : "]");
		return res.toString();
	}
	
	@Override
	public void onTimer() {
		UhcGameManager gameManager = UhcGameManager.instance;
		if (round == 0) {
			for (UhcGamePlayer player : gameManager.getUhcPlayerManager().getAllPlayers())
				player.getRealPlayer().ifPresent(playermp -> playermp.setGameMode(GameMode.SPECTATOR));
		}

		while (true) {
			float max = 0;
			EnumStat stat = EnumStat.values()[round];
			List<Pair<UhcGamePlayer, Float>> stats = Lists.newArrayList();
			for (UhcGamePlayer player : gameManager.getUhcPlayerManager().getCombatPlayers()) {
				float value = player.getStat().getFloatStat(stat);
				max = Math.max(max, value);
				stats.add(Pair.of(player, value));
			}

			stats.sort((A, B) -> B.getValue().compareTo(A.getValue()));
			for (int i = 0; i < stats.size(); i++) {
				Pair<UhcGamePlayer, Float> pair = stats.get(i);
				if (pair.getValue() == 0 || (i >= 8 && pair.getValue() < stats.get(i - 1).getValue())) {
					stats.subList(i, stats.size()).clear();
					break;
				}
			}

			if (!stats.isEmpty()) {
				gameManager.broadcastMessage(stat.name + ":");
				for (Pair<UhcGamePlayer, Float> pair : stats) {
					gameManager.broadcastMessage(String.format("  %s%-40s %7.2f %s", pair.getLeft().getTeam().getTeamColor().chatColor,
							getGraph((int) (40 * pair.getValue() / max)), pair.getValue(), pair.getLeft().getName()));
				}
			}

			if (++round == EnumStat.values().length)
				setCanceled();
			else if (stats.isEmpty())
				continue;

			break;
		}
	}

}
