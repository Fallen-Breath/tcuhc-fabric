/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.options.Options;
import me.fallenbreath.tcuhc.task.Task.TaskTimer;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class TaskScoreboard extends TaskTimer {
	
	private final int borderStart, borderEnd;
	private final int gameTime, startTime, endTime, netherTime, caveTime;
	
	public static final String scoreName = "time";
	public static final String displayName = "UHC Game";
	public static final String[] lines = { "Time Remaining:", "Border Radius:", "Nether Close:", "Cave Close:" };
	
	private Scoreboard scoreboard;
	private ScoreboardObjective objective;
	
	public TaskScoreboard() {
		super(0, 20);
		
		Options options = UhcGameManager.instance.getOptions();
		borderStart = options.getIntegerOptionValue("borderStart");
		borderEnd = options.getIntegerOptionValue("borderEnd");
		gameTime = options.getIntegerOptionValue("gameTime");
		startTime = options.getIntegerOptionValue("borderStartTime");
		endTime = options.getIntegerOptionValue("borderEndTime");
		netherTime = options.getIntegerOptionValue("netherCloseTime");
		caveTime = options.getIntegerOptionValue("caveCloseTime");
		
		scoreboard = UhcGameManager.instance.getMainScoreboard();
		if ((objective = scoreboard.getObjective(scoreName)) == null) {
			objective = scoreboard.addObjective(scoreName, ScoreboardCriterion.DUMMY, new LiteralText(displayName), ScoreboardCriterion.RenderType.INTEGER);
		}
		scoreboard.setObjectiveSlot(1, objective);
		scoreboard.getPlayerScore(lines[0], objective).setScore(gameTime);
		scoreboard.getPlayerScore(lines[1], objective).setScore(borderStart / 2);
		scoreboard.getPlayerScore(lines[2], objective).setScore(netherTime);
		scoreboard.getPlayerScore(lines[3], objective).setScore(caveTime);
		
		UhcGameManager.instance.addTask(new TaskNetherCave());
	}
	
	private int getBorderPosition() {
		return (int) UhcGameManager.instance.getOverWorld().getWorldBorder().getSize();
	}
	
	@Override
	public void onTimer() {
		if (this.hasFinished() || !UhcGameManager.instance.isGamePlaying()) this.setCanceled();
		ScoreboardPlayerScore score = scoreboard.getPlayerScore(lines[0], objective);
		int timeRemaining = score.getScore();
		score.setScore(timeRemaining - 1);
		if (timeRemaining == gameTime - startTime) {
			UhcGameManager.instance.addTask(new TaskBorderReminder());
		}
		scoreboard.getPlayerScore(lines[1], objective).setScore(getBorderPosition() / 2);

		score = scoreboard.getPlayerScore(lines[2], objective);
		if (score.getScore() > 0)
			score.setScore(Math.max(0, score.getScore() - 1));
		else scoreboard.resetPlayerScore(lines[2], objective);

		score = scoreboard.getPlayerScore(lines[3], objective);
		if (score.getScore() > 0)
			score.setScore(Math.max(0, score.getScore() - 1));
		else scoreboard.resetPlayerScore(lines[3], objective);

		if (timeRemaining % 60 == 0) updateCompassRotation();
	}
	
	private void updateCompassRotation() {
		for (UhcGamePlayer player : UhcGameManager.instance.getUhcPlayerManager().getCombatPlayers()) {
			if (player.isAlive()) {
				if (player.getRealPlayer().isPresent()) {
					ServerPlayerEntity playermp = player.getRealPlayer().get();
					ServerPlayerEntity target = null;
					for (UhcGamePlayer tmpTarget : UhcGameManager.instance.getUhcPlayerManager().getCombatPlayers()) {
						if (tmpTarget.isAlive() && player.getTeam() != tmpTarget.getTeam() && tmpTarget.getRealPlayer().isPresent()) {
							if (target == null || playermp.squaredDistanceTo(target) > playermp.squaredDistanceTo(tmpTarget.getRealPlayer().get()))
								target = tmpTarget.getRealPlayer().get();
						}
					}
					if (target != null)
						playermp.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(target.getBlockPos(), 0));
				}
			}
		}
	}
	
	public static void hideScoreboard() {
		UhcGameManager.instance.getMainScoreboard().setObjectiveSlot(1, null);
	}

}
