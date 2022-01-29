package me.fallenbreath.tcuhc.util.mmr;

import com.google.common.collect.Maps;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

public class TeamAllocator {

	public static Map<UhcGamePlayer, Integer> randomMatching(Map<UhcGamePlayer, Double> playerScores, int teamNums) {
		Map<UhcGamePlayer, Integer> result = Maps.newHashMap();
		List<UhcGamePlayer> playerList = new ArrayList<>(playerScores.keySet());
		Collections.shuffle(playerList);
		for (int i = 0; i < playerList.size(); i++) {
			result.put(playerList.get(i), i % teamNums);
		}
		return result;
	}

	public static Map<UhcGamePlayer, Integer> samplingMatching(Map<UhcGamePlayer, Double> playerScores, int teamNums, int sampleTimes) {
		if (playerScores.isEmpty() || teamNums <= 0 || sampleTimes <= 0) {
			throw new RuntimeException(String.format("Invalid sampling arguments: map size=%d, teamNums=%d, samplingTimes=%d", playerScores.size(), teamNums, sampleTimes));
		}

		//do k times sampling, find the result with the lowest team performance point STD
		Map<UhcGamePlayer, Integer> bestMatching = null;
		double bestStd = -1;
		for (int i = 0; i < sampleTimes; i++) {
			Map<UhcGamePlayer, Integer> matching = randomMatching(playerScores, teamNums);
			int[] playerAmount = new int[teamNums];
			double[] ppSums = new double[teamNums];

			// calculate average performance point of team members for each team
			matching.forEach((player, teamId) -> {
				ppSums[teamId] += playerScores.get(player);
				playerAmount[teamId] += 1;
			});
			List<Double> ppAveranges = Lists.newArrayList();
			for (int j = 0; j < ppSums.length; j++) {
				if (playerAmount[j] > 0) {
					ppAveranges.add(ppSums[j] / playerAmount[j]);
				}
			}

			// calculate standard deviation of performance points of all teams, and updating the result
			double std = getStandardDeviation(ppAveranges);
			if (bestMatching == null || std < bestStd) {
				bestMatching = matching;
				bestStd = std;
			}
		}
		return bestMatching;
	}

	private static double getStandardDeviation(List<Double> data) {
		double average = getAverage(data);
		double sum = 0;
		for (double value : data) {
			double delta = value - average;
			sum += delta * delta;
		}
		return Math.sqrt(sum);
	}

	private static double getAverage(List<Double> data) {
		double sum = 0;
		for (double value : data) {
			sum += value;
		}
		return sum / data.size();
	}
}
