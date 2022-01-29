package me.fallenbreath.tcuhc.util.mmr;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fallenbreath.tcuhc.TcUhcMod;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;

import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MatchMakingRatingHandler {
	final private File file;
	final private static MatchMakingRatingHandler INSTANCE = new MatchMakingRatingHandler("player_mmr.json");
	private PlayerMMRDataStorage playerData;

	private static final double WIN_STREAK_FACTOR = 1.1F;
	private static final double BASE_INCREMENT = 0.05F;

	public MatchMakingRatingHandler(String fileName) {
		this.file = TcUhcMod.getConfigPath().resolve(fileName).toFile();
		this.readData();
	}

	public static MatchMakingRatingHandler getInstance() {
		return INSTANCE;
	}

	private void readData() {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file))) {
			playerData = new Gson().fromJson(reader, PlayerMMRDataStorage.class);
			UhcGameManager.LOG.info("Loaded player MMR data");
		} catch (Exception e) {
			if (e instanceof FileNotFoundException) {
				UhcGameManager.LOG.warn("Generating new player MMR");
			} else {
				UhcGameManager.LOG.error("Error during read player MMR file", e);
			}
			playerData = new PlayerMMRDataStorage();
		}
		this.saveData();
	}

	public void saveData() {
		// sort with pp to make the file looks nicer
		PlayerMMRDataStorage sorted = new PlayerMMRDataStorage();
		playerData.entrySet().stream().
				sorted(Collections.reverseOrder(Comparator.comparingDouble(e -> e.getValue().getPerformancePoint()))).
				forEachOrdered(e -> sorted.put(e.getKey(), e.getValue()));
		playerData = sorted;

		try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file))) {
			writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(playerData));
		} catch (Exception e) {
			UhcGameManager.LOG.error("Error during save player MMR file", e);
		}
	}

	private PlayerMMRDataStorage.MMRItem getPlayerData(UhcGamePlayer player) {
		return playerData.computeIfAbsent(player.getPlayerUUID(), uuid -> new PlayerMMRDataStorage.MMRItem(player.getName()));
	}

	private double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}

	public void updatePlayerMMR(UhcGamePlayer player, int teamAmount, int teamRank) {
		PlayerMMRDataStorage.MMRItem playerData = getPlayerData(player);
		int winStreak = teamRank == 1 ? playerData.getWinStreak() + 1 : 0;
		playerData.setWinStreak(winStreak);
		double prevPP = playerData.getPerformancePoint();
		double ppIncrement = getPerformancePointIncrement(teamAmount, teamRank) * Math.pow(WIN_STREAK_FACTOR, winStreak) + BASE_INCREMENT;
		playerData.addPerformancePoint(ppIncrement);
		double ppDelta = playerData.getPerformancePoint() - prevPP;
		UhcGameManager.LOG.info(
				"Performance point update for {}: {} -> {} ({})",
				player.getName(), round(prevPP), round(playerData.getPerformancePoint()),
				(ppDelta >= 0 ? "+" : "-") + round(Math.abs(ppDelta))
		);
	}

	private static double getPerformancePointIncrement(int teamAmount, int teamRank) {
		// 2 teams: -0.5, 0.5
		// 3 teams: -1, 0, 1
		// 4 teams: -1.5, -0.5, 0.5, 1.5
		double upperBound = teamAmount / 2.0 - 0.5;
		return upperBound - (teamRank - 1);
	}

	public Map<UhcGamePlayer, Double> getPlayerWithScore(List<UhcGamePlayer> combatPlayers) {
		Map<UhcGamePlayer, Double> result = Maps.newHashMap();
		for (UhcGamePlayer player : combatPlayers) {
			result.put(player, getPlayerData(player).getPerformancePoint());
		}
		return result;
	}

}
