/*
 From Gamepiaynmo: https://github.com/Gamepiaynmo/TC-UHC
 */

package me.fallenbreath.tcuhc.task;

import com.google.common.collect.Lists;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.options.Options;
import me.fallenbreath.tcuhc.task.Task.TaskTimer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TaskNetherCave extends TaskTimer {
	
	private final int netherCloseTime, caveCloseTime, gameTime, borderFinal;

	private int finalX, finalZ, finalTime, finalMinY, finalMaxY;

	public static final String[] lines = { "Border Min Y:", "Border Max Y:", "Border Center X:", "Border Center Z:" };

	public TaskNetherCave() {
		super(0, 20);
		Options options = Options.instance;
		netherCloseTime = options.getIntegerOptionValue("netherCloseTime");
		caveCloseTime = options.getIntegerOptionValue("caveCloseTime");
		gameTime = options.getIntegerOptionValue("gameTime");
		borderFinal = UhcGameManager.instance.getOptions().getIntegerOptionValue("borderFinal");
	}
	
	@Override
	public void onTimer() {
		if (this.hasFinished()) return;
		if (!UhcGameManager.instance.isGamePlaying() || UhcGameManager.instance.hasGameEnded()) this.setCanceled();
		int timeRemaining = UhcGameManager.instance.getGameTimeRemaining();
		int timePast = gameTime - timeRemaining;
//		if (timeRemaining == 0) this.setCanceled();
		Iterable<UhcGamePlayer> combatPlayers = UhcGameManager.instance.getUhcPlayerManager().getCombatPlayers();
		
		int netherTime = netherCloseTime - timePast;
		if (netherTime > 0 && netherTime <= 120 && netherTime % 30 == 0) {
			UhcGameManager.instance.broadcastMessage(Formatting.DARK_RED + "Nether will be closed in " + netherTime + " seconds.");
		} else if (netherTime == 0) {
			UhcGameManager.instance.broadcastMessage(Formatting.DARK_RED + "Nether closed.");
		} else if (netherTime < 0) {
			for (UhcGamePlayer player : combatPlayers) {
				player.getRealPlayer().ifPresent(playermp -> {
					if (playermp.dimension != DimensionType.OVERWORLD)
						playermp.damage(DamageSource.IN_WALL, 1.0f);
				});
			}
		}
		
		int caveTime = caveCloseTime - timePast;
		if (caveTime > 0 && caveTime <= 120 && caveTime % 30 == 0) {
			UhcGameManager.instance.broadcastMessage(Formatting.DARK_RED + "Caves will be closed in " + caveTime + " seconds.");
		} else if (caveTime == 0) {
			UhcGameManager.instance.broadcastMessage(Formatting.DARK_RED + "Caves closed.");
			WorldBorder border = UhcGameManager.instance.getOverWorld().getWorldBorder();
			int finalSize = Math.max((int) border.getSize() / 2, 1);
			Random random = new Random();
			while (finalX * finalX + finalZ * finalZ < finalSize * finalSize / 4) {
				finalX = random.nextInt(finalSize * 2) - finalSize;
				finalZ = random.nextInt(finalSize * 2) - finalSize;
			}

			finalTime = Math.max(gameTime - caveCloseTime, finalSize * 2 - borderFinal);
			border.interpolateSize(border.getSize(), borderFinal, finalTime * 1000L);

			ServerWorld world = UhcGameManager.instance.getOverWorld();
			List<Integer> heights = Lists.newArrayList();
			int sampleSize = borderFinal / 2;
			int step = Math.max(1, sampleSize / 4);
			for (int x = finalX - sampleSize; x <= finalX + sampleSize; x += step) {
				for (int z = finalZ - sampleSize; z <= finalZ + sampleSize; z += step) {
					for (int y = 255; y > 0; y--) {
						BlockState state = world.getBlockState(new BlockPos(x, y, z));
						if (state.getBlock() == Blocks.STONE && state.getMaterial() == Material.STONE) {
							heights.add(y);
							break;
						}
						if (state.getFluidState().matches(FluidTags.WATER)) {
							heights.add(Math.max(1, y - 4));
							break;
						}
					}
				}
			}

			Collections.sort(heights);
			finalMinY = heights.get(4);
			finalMaxY = heights.get(heights.size() - 4) + 12;

			LevelProperties worldinfo = world.getLevelProperties();
			worldinfo.setClearWeatherTime(gameTime * 20);
			worldinfo.setRainTime(0);
			worldinfo.setThunderTime(0);
			worldinfo.setRaining(false);
			worldinfo.setThundering(false);

			GameRules gameRules = world.getGameRules();
			gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, world.getServer());
			world.setTimeOfDay(1000);
		} else if (caveTime < 0) {
			caveTime = -caveTime;
			boolean glow = caveTime % 60 == 0;
			float partial = caveTime <= finalTime ? (float) caveTime / finalTime : 1;
			float minY = finalMinY * partial;
			float maxY = 255 - partial * (255 - finalMaxY);

			Scoreboard scoreboard = UhcGameManager.instance.getMainScoreboard();
			ScoreboardObjective objective = scoreboard.getObjective(TaskScoreboard.scoreName);
			scoreboard.getPlayerScore(lines[0], objective).setScore((int) Math.ceil(minY));
			scoreboard.getPlayerScore(lines[1], objective).setScore((int) Math.floor(maxY));
			scoreboard.getPlayerScore(lines[2], objective).setScore(Math.round(partial * finalX));
			scoreboard.getPlayerScore(lines[3], objective).setScore(Math.round(partial * finalZ));

			for (UhcGamePlayer player : combatPlayers) {
				player.getRealPlayer().ifPresent(playermp -> {
					if (glow) playermp.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 1, false, false));
					if (playermp.getPos().getY() < minY || playermp.getPos().getY() > maxY)
						playermp.damage(DamageSource.IN_WALL, 1.0f);

					double particleY = -1;
					if (playermp.getPos().getY() < minY + 5)
						particleY = minY + 3;
					if (playermp.getPos().getY() > maxY - 5)
						particleY = maxY - 2;
					if (particleY > 0) {
						playermp.getServerWorld().spawnParticles(playermp, ParticleTypes.PORTAL, false,
								playermp.getPos().getX(), particleY, playermp.getPos().getZ(), 100, 2, 0, 2, 0);
					}
				});
			}

			if (caveTime <= finalTime) {
				WorldBorder border = UhcGameManager.instance.getOverWorld().getWorldBorder();
				border.setCenter(partial * finalX, partial * finalZ);
			}
		}
	}

}
