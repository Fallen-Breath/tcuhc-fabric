package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import org.apache.logging.log4j.Level;
import org.lwjgl.system.CallbackI;

import java.util.*;

public class TeamAllocator {
	public static TeamAllocator teamAllocator = new TeamAllocator();

	private TeamAllocator() {

	}

	public static TeamAllocator getTeamAllocator() {
		return teamAllocator;
	}

	/**
	 * @param players  player map with "UhcGamePlayer" as key and performance point as value
	 * @param teamNums number of teams that matchmaking need allocate
	 * @return List of List that contains allocated team
	 * <p>
	 * the returned team list is allocated randomly
	 */
	public List<List<UhcGamePlayer>> TotalRandomMatchMaking(Map<UhcGamePlayer, Double> players, int teamNums) {
		ArrayList<List<UhcGamePlayer>> result = new ArrayList();
		//init the result list
		for (int i = 0; i < teamNums; i++) {
			result.add(new ArrayList<>());
		}
		ArrayList<UhcGamePlayer> incoming_List = new ArrayList<>();
		incoming_List.addAll(players.keySet());
		shuffle(incoming_List);
		//shuffle
		while (incoming_List.size()>0){
			for (int i = 0; i < teamNums; i++) {
				if (incoming_List.size()>0){
					result.get(i).add(incoming_List.remove(incoming_List.size()-1));
				}
			}
		}

		return result;
	}

	/**
	 * @param players  player map with "UhcGamePlayer" as key and performance point as value
	 * @param teamNums number of teams that matchmaking need allocate
	 * @return List of List that contains allocated team
	 * <p>
	 * the returned result is the one of multiple totally random samplings that has lowest std.
	 */
	public List<List<UhcGamePlayer>> SamplingMatchmaking(Map<UhcGamePlayer, Double> players, int teamNums, int sampling_k) {
		List<List<UhcGamePlayer>> sampling_result[] = new ArrayList[sampling_k];
		double minStd = Double.MAX_VALUE;
		int bestResult = 0;
		//do k times sampling, find the result with lowest STD
		double debugArray[] = new double[sampling_k];
		for (int i = 0; i < sampling_k; i++) {
			//get k result from random sampling and store it.
			sampling_result[i] = TotalRandomMatchMaking(players, teamNums);
			ArrayList<Double> teamPPs = new ArrayList<>();
			//get pp from current set
			for (int j = 0; j < teamNums; j++) {
				double localTeamPP = 0;
				//get pp from each team;
				for (int k = 0; k < sampling_result[i].get(j).size(); k++) {
					localTeamPP += players.get(sampling_result[i].get(j).get(k));
				}
				teamPPs.add(localTeamPP);
			}
			double currentSet_std = std(teamPPs);
			debugArray[i] = currentSet_std;
			if (minStd > currentSet_std) {
				minStd = currentSet_std;
				bestResult = i;
			}
		}
		UhcGameManager.LOG.log(Level.DEBUG,"------------STD result start----------------");
		StringBuilder stdMessage = new StringBuilder();
		stdMessage.append("STDs:");
		for (int i=0;i<debugArray.length;i++){
			stdMessage.append(" "+debugArray[i]);
		}
		UhcGameManager.LOG.log(Level.DEBUG,stdMessage.toString());
		UhcGameManager.LOG.log(Level.DEBUG,"chosen std:"+minStd);
		UhcGameManager.LOG.log(Level.DEBUG,"------------STD result end----------------");

		return sampling_result[bestResult];
	}

	/**
	 * @param players  player map with "UhcGamePlayer" as key and performance point as value
	 * @param teamNums number of teams that matchmaking need allocate
	 * @return List of List that contains allocated team
	 * <p>
	 * the returned result is based on greedy matchmaking
	 */
	public List<List<UhcGamePlayer>> NonRandomMatchMaking(Map<UhcGamePlayer, Double> players, int teamNums) {
		List<List<UhcGamePlayer>> result = new ArrayList();
		for (int i = 0; i < teamNums; i++) {
			result.add(new ArrayList<>());
		}
		LinkedHashMap<UhcGamePlayer, Double> sortedPlayers = new LinkedHashMap<>();
		players.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedPlayers.put(x.getKey(), x.getValue()));

		double[] scores = new double[teamNums];
		int expectedPlayerNum = (players.keySet().size() / teamNums) + 1;
		for (UhcGamePlayer player : sortedPlayers.keySet()) { //从分数最高的玩家到分数最低的玩家
			int weekTeam = 0;
			for (int i = 0; i < teamNums; i++) { //选择一个最弱的队,并且这个队没有满员
				if (scores[weekTeam] > scores[i] && result.get(i).size() <= expectedPlayerNum) {
					weekTeam = i;
				}
			}
			result.get(weekTeam).add(player); //将这个玩家分配给最弱的队
			scores[weekTeam] += players.get(player);
		}
		return result;
	}

	/**
	 * @param a Arraylist that contain players
	 *          <p>
	 *          randomized Arraylist
	 */

	private void shuffle(ArrayList<UhcGamePlayer> a) {
		for (int i = a.size() - 1; i > 0; i--) {
			int j = (int) (Math.random() * i);
			UhcGamePlayer t = a.get(i);
			a.set(i, a.get(j));
			a.set(j, t);
		}
	}

	/**
	 * @param data double arraylist
	 * @return the std value of the incoming data.
	 */

	private double std(ArrayList<Double> data) {

		double mean = mean(data);
		double t1 = 0;
		for (double i : data) {
			double t2 = i - mean;
			t1 += t2 * t2;
		}
		return Math.sqrt(t1);

	}

	/**
	 * @param data double arraylist
	 * @return the mean value of the incoming data.
	 */

	private double mean(ArrayList<Double> data) {
		double sum = 0;
		for (double i : data) {
			sum += i;
		}
		return sum / data.size();
	}
}
