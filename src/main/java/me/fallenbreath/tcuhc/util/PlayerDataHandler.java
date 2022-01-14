package me.fallenbreath.tcuhc.util;

import com.google.gson.Gson;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataHandler {
    final private String path;
    final private static PlayerDataHandler playerDataHandler = new PlayerDataHandler("Data/playerData.txt");
    final private Gson gson = new Gson();
    private Map<String,PlayerMatchMakingData> playerData;
    public PlayerDataHandler(String path){
        this.path = path;
        this.init();
    }
    public static PlayerDataHandler getDataBase() {
        return playerDataHandler;
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
        }
        catch (IOException e){
            System.out.println("Fail to init database");
        }
    }
    private void saveData(){
        try{
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            for (String s:playerData.keySet()){
                out.write(pack(playerData.get(s)));
                out.write("\n");
            }
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

    public Map<String, PlayerMatchMakingData> getParkingMap() {
        return playerData;
    }
}
