package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGamePlayer;
import org.lwjgl.system.CallbackI;

import java.util.*;

public class TeamAllocator {
    public static TeamAllocator teamAllocator = new TeamAllocator();
    private TeamAllocator() {

    }

    public static TeamAllocator getTeamAllocator(){
        return teamAllocator;
    }

    public List<List<UhcGamePlayer>>  TotalRandomMatchMaking(Map<UhcGamePlayer,Double> players,int teamNums){
        ArrayList<List<UhcGamePlayer>> result = new ArrayList();
        //init the result list
        for (int i=0;i<teamNums;i++){
            result.add(new ArrayList<>());
        }
        ArrayList<UhcGamePlayer> incoming_List = new ArrayList<>();
        for (UhcGamePlayer player:players.keySet()){
            incoming_List.add(player);
        }
        shuffle(incoming_List);
        //shuffle
        for (int i=0;i<teamNums;i++){
            result.get(i).add(incoming_List.remove(players.size()-1));
        }

        return result;
    }
    public List<List<UhcGamePlayer>>  SamplingMatchmaking(Map<UhcGamePlayer,Double> players,int teamNums,int sampling_k){
        List<List<UhcGamePlayer>> result = new ArrayList();
        List<List<UhcGamePlayer>> sampling_result[] = new ArrayList[sampling_k];
        double minStd = Double.MAX_VALUE;
        int bestResult = 0;
        //init the result list
        for (int i=0;i<teamNums;i++){
            result.add(new ArrayList<>());
        }
        //do k times sampling, find the result with lowest STD
        for (int i=0;i<sampling_k;i++){
            //get k result from random sampling and store it.
            sampling_result[i] = TotalRandomMatchMaking(players,teamNums);
            ArrayList<Double> teamPPs = new ArrayList<>();
            //get pp from current set
            for (int j=0;j<teamNums;j++){
                double localTeamPP = 0;
                //get pp from each team;
                for (int k=0;k<sampling_result[i].get(j).size();k++){
                    localTeamPP += players.get(sampling_result[i].get(j).get(k));
                }
                teamPPs.add(localTeamPP);
            }
            double currentSet_std = std(teamPPs);
            if (minStd>currentSet_std){
                minStd = currentSet_std;
                bestResult = i;
            }
        }
        return sampling_result[bestResult];
    }
    public List<List<UhcGamePlayer>> NonRandomMatchMaking(Map<UhcGamePlayer,Double> players, int teamNums){
        List<List<UhcGamePlayer>> result = new ArrayList();
        for (int i=0;i<teamNums;i++){
            result.add(new ArrayList<>());
        }
        LinkedHashMap<UhcGamePlayer,Double> sortedPlayers = new LinkedHashMap<>();
        players.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedPlayers.put(x.getKey(),x.getValue()));

        double[] scores = new double[teamNums];
        int expectedPlayerNum = (players.keySet().size()/teamNums)+1;
        for (UhcGamePlayer player: sortedPlayers.keySet()){ //从分数最高的玩家到分数最低的玩家
            int weekTeam = 0;
            for (int i=0;i<teamNums;i++){ //选择一个最弱的队,并且这个队没有满员
                if (scores[weekTeam]>scores[i]&&result.get(i).size()<=expectedPlayerNum){
                    weekTeam = i;
                }
            }
            result.get(weekTeam).add(player); //将这个玩家分配给最弱的队
            scores[weekTeam]+=players.get(player);
        }
        return result;
    }
    /**
     *  randomlize players
     *  随机玩家
     * */

    private void shuffle(ArrayList<UhcGamePlayer> a){
        for (int i=a.size()-1;i>0;i--){
            int j = (int)(Math.random()*i);
            UhcGamePlayer t = a.get(i);
            a.set(i,a.get(j));
            a.set(j,t);
        }
    }
    /**
     * find the std value of the incoming data.
     * 计算数据标准差
     * */

    private double std(ArrayList<Double> data){

        double mean = mean(data);
        double t1 = 0;
        for (double i:data){
            double t2 = i-mean;
            t1 += t2*t2;
        }
        double std = Math.sqrt(t1);
        return std;
    }
    /**
     * find the mean value of the incoming data
     * 计算平均数
     * */

    private double mean (ArrayList<Double> data){
        double sum = 0;
        for (double i:data){
            sum += i;
        }
        double mean = sum/data.size();
        return mean;
    }
}
