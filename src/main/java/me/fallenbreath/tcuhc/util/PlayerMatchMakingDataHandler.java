package me.fallenbreath.tcuhc.util;

import com.google.gson.Gson;
import me.fallenbreath.tcuhc.UhcGamePlayer;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerMatchMakingDataHandler {
    /**
     *  队伍分配影响因素:
     *  输出/承伤系数
     *
     *  输出总量
     *
     *  分数计算 = 系数 * 输出总量
     *
     *  连胜/连败
     *
     *
     *
     *
     * */
    static double k_singleGame = 0.2; // pp更新常数 越大说明单局游戏的表现影响越高
    static double k_wStreak = 1.025; // 连胜影响系数 越大说明连胜/连败影响越大
    final private String path;
    final private static PlayerMatchMakingDataHandler PLAYER_MATCH_MAKING_DATA_HANDLER = new PlayerMatchMakingDataHandler("playerData.txt");
    final private Gson gson = new Gson();
    private Map<UUID,PlayerMatchMakingData> playerData;
    public PlayerMatchMakingDataHandler(String path){
        this.path = path;
        File file = new File(path);
        try{
            boolean value = file.createNewFile();
            if (value){
                System.out.println("new File created");
            }else {
                System.out.println("File existed");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
    }
    public static PlayerMatchMakingDataHandler getDataBase() {
        return PLAYER_MATCH_MAKING_DATA_HANDLER;
    }

    private void init(){
        playerData = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String line;
            while ((line = in.readLine())!= null){
                PlayerMatchMakingData data = unpack(line);
                playerData.put(data.getUUID(),data);
            }
            in.close();
            System.out.println("database init successfully");
        }
        catch (IOException e){
            System.out.println("Fail to init database");
        }
    }
    public void saveData(){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            for (UUID s:playerData.keySet()){
                out.write(pack(playerData.get(s)));
                out.write("\n");
            }
            System.out.println("data saved successfully");
            out.close();
        }catch (IOException e){
            System.out.println("Fail to save database");
        }
    }
    private PlayerMatchMakingData unpack(String str){
        return gson.fromJson(str,PlayerMatchMakingData.class);
    }
    private String pack (PlayerMatchMakingData data){
        return gson.toJson(data);
    }

    public boolean add(PlayerMatchMakingData data){
        if (playerData.containsKey(data.getUUID())){
            return false;
        }
        playerData.put(data.getUUID(),data);
        saveData();
        return true;
    }

    /**
     *  pp: 技能分数
     *  SGPP: 当前pp
     *  hisPP: 历史pp
     *  wStreak: 正为连胜 负为连败
     *  k: 影响系数
     *
     * 更新所有玩家的技能分数
     *
     */

    public void updatePersonalPP (double SGPP,UUID uuid){
        double hisPP = playerData.get(uuid).getHisPP();
        int wStreak = playerData.get(uuid).getWStreak();
        playerData.get(uuid).setHisPP((1-k_singleGame)*hisPP+(k_singleGame*SGPP*Math.pow(k_wStreak,(wStreak-1))));
    }

     /**
     * 更新玩家的连胜数据
     * */
    public void processWinStreak(UUID UUID,boolean win){
        int winStreak = playerData.get(UUID).getWStreak();

        if (winStreak<0&&win||winStreak>0&&!win){
            winStreak = 0;
        }else {
            if (win){
                winStreak++;
            }else {
                winStreak--;
            }
        }
        playerData.get(UUID).setWStreak(winStreak);
    }
    /**
     * 输入： 参与游戏的玩家列表
     * 输出:  一个Map, Key:玩家， value:玩家的PP值
     *
     * */
    public Map<UhcGamePlayer,Double> getPlayerWithScore(List<UhcGamePlayer> combatPlayers){
        Map<UhcGamePlayer,Double> result = new HashMap<>();
        for (UhcGamePlayer player:combatPlayers){
            UUID uuid = player.getPlayerUUID();
            if (playerData.containsKey(uuid)){ // 检测该玩家是否存在
                result.put(player,playerData.get(uuid).getHisPP());
            }else {
                playerData.put(uuid,new PlayerMatchMakingData(uuid,0,100)); //初始的PP值为100
                result.put(player,playerData.get(uuid).getHisPP());

            }
        }
        return result;
    }
    public Map<UUID, PlayerMatchMakingData> getPlayerData() {
        return playerData;
    }
}
