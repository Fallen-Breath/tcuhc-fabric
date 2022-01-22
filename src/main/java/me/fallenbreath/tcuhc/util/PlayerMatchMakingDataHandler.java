package me.fallenbreath.tcuhc.util;

import com.google.gson.Gson;
import me.fallenbreath.tcuhc.UhcGameManager;
import me.fallenbreath.tcuhc.UhcGamePlayer;
import me.fallenbreath.tcuhc.options.Options;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerMatchMakingDataHandler {
	final private String path;
	final private static PlayerMatchMakingDataHandler PLAYER_MATCH_MAKING_DATA_HANDLER = new PlayerMatchMakingDataHandler("playerData.txt");
	final private Gson gson = new Gson();
	private Map<UUID, PlayerMatchMakingData> playerData;
	private final Options uhcOptions;
	private final double initPerformancePoint = 100;

	public PlayerMatchMakingDataHandler(String path) {
		this.path = path;
		uhcOptions = Options.instance;
		File file = new File(path);
		try {
			boolean value = file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			UhcGameManager.LOG.log(Level.WARN,"Error during create/read file");
		}
		this.init();
	}

	public static PlayerMatchMakingDataHandler getDataBase() {
		return PLAYER_MATCH_MAKING_DATA_HANDLER;
	}

	private void init() {
		playerData = new HashMap<>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line;
			while ((line = in.readLine()) != null) {
				PlayerMatchMakingData data = unpack(line);
				playerData.put(data.getUUID(), data);
			}
			in.close();
		} catch (IOException e) {
			UhcGameManager.LOG.log(Level.WARN,"Error during read player MMR file");
		}
	}

	public void saveData() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(path));
			for (UUID s : playerData.keySet()) {
				out.write(pack(playerData.get(s)));
				out.write("\n");
			}
			out.close();
		} catch (IOException e) {
			UhcGameManager.LOG.log(Level.WARN,"Error during save player MMR file");
		}
	}

	/**
	 * @param str json object in the string format
	 * @return a PlayerMatchMakingData object that unpacked from this String
	 */
	private PlayerMatchMakingData unpack(String str) {
		return gson.fromJson(str, PlayerMatchMakingData.class);
	}

	/**
	 * @param data a PlayerMatchMakingData object
	 * @return the json object in the string format
	 */

	private String pack(PlayerMatchMakingData data) {
		return gson.toJson(data);
	}

	/**
	 * @param data a PlayerMatchMakingData object
	 * @return if the data added successfully
	 */
	public boolean add(PlayerMatchMakingData data) {
		if (playerData.containsKey(data.getUUID())) {
			return false;
		}
		playerData.put(data.getUUID(), data);
		saveData();
		return true;
	}

	/**
	 * @param SingleGame_PP player's performance point in the current game
	 * @param uuid          player's uuid relative to current performance point
	 */
	public void updatePersonalPP(double SingleGame_PP, UUID uuid) {
		double k_singleGame = uhcOptions.getFloatOptionValue("k_singleGame");
		double k_wStreak = uhcOptions.getFloatOptionValue("k_wStreak");
		double hisPP = playerData.get(uuid).getHisPP();
		int wStreak = playerData.get(uuid).getWStreak();
		playerData.get(uuid).setHisPP((1 - k_singleGame) * hisPP + (k_singleGame * SingleGame_PP * Math.pow(k_wStreak, (wStreak - 1))));
	}

	/**
	 * @param win  if the player win in the current game
	 * @param uuid player's uuid relative to current performance point
	 */
	public void processWinStreak(UUID uuid, boolean win) {
		int winStreak = playerData.get(uuid).getWStreak();

		if (winStreak<0)
		{
			if (win)
			{
				winStreak /= 2;
			}else
			{
				winStreak--;
			}
		}
		else
		{
			if (win)
			{
				winStreak++;
			}else
			{
				winStreak /= 2;
			}
		}
		playerData.get(uuid).setWStreak(winStreak);
	}

	/**
	 * @param combatPlayers A list of player that joined the game
	 * @return Map with UhcGamePlayer as key and performance point as key
	 */
	public Map<UhcGamePlayer, Double> getPlayerWithScore(List<UhcGamePlayer> combatPlayers) {
		Map<UhcGamePlayer, Double> result = new HashMap<>();
		double k_point_factor = uhcOptions.getFloatOptionValue("k_point_factor");
		for (UhcGamePlayer player : combatPlayers) {
			UUID uuid = player.getPlayerUUID();
			if (!playerData.containsKey(uuid)) {// 检测该玩家是否存在
				playerData.put(uuid, new PlayerMatchMakingData(uuid, 0, initPerformancePoint,player.getName())); //初始的PP值为100
			}
			double MPP = Math.pow(playerData.get(uuid).getHisPP(), k_point_factor);
			result.put(player, MPP);
		}
		return result;
	}

	public Map<UUID, PlayerMatchMakingData> getPlayerData() {
		return playerData;
	}

}
