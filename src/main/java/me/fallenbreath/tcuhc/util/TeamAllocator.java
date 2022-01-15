package me.fallenbreath.tcuhc.util;

import me.fallenbreath.tcuhc.UhcGamePlayer;
import org.lwjgl.system.CallbackI;

import java.util.*;

public class TeamAllocator {
    final double k_point_factor = 1.5; // 战力修正值, 越大的话说明战斗力质量越重要.
    public static TeamAllocator teamAllocator = new TeamAllocator();
    private TeamAllocator() {

    }

    public static TeamAllocator getTeamAllocator(){
        return teamAllocator;
    }
    public List<List<UhcGamePlayer>> matchMaking(Map<UhcGamePlayer,Double> players, int teamNums){
        List<List<UhcGamePlayer>> res = new ArrayList();
        for (int i=0;i<teamNums;i++){
            res.add(new ArrayList<>());
        }
        LinkedHashMap<UhcGamePlayer,Double> sortedPlayers = new LinkedHashMap<>();
        players.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedPlayers.put(x.getKey(),x.getValue()));

        double[] scores = new double[teamNums];
        int expectedPlayerNum = (players.keySet().size()/teamNums)+1;
        for (UhcGamePlayer player: sortedPlayers.keySet()){ //从分数最高的玩家到分数最低的玩家
            int weekTeam = 0;
            for (int i=0;i<teamNums;i++){ //选择一个最弱的队,并且这个队没有满员
                if (scores[weekTeam]>scores[i]&&res.get(i).size()<=expectedPlayerNum){
                    weekTeam = i;
                }
            }
            res.get(weekTeam).add(player); //将这个玩家分配给最弱的队
            scores[weekTeam]+=players.get(player);
        }
        return res;
    }

    /**
     * 计算标准差和平均数
     *
     * */
    public double std(ArrayList<Double> data){

        double mean = mean(data);
        double t1 = 0;
        for (double i:data){
            double t2 = i-mean;
            t1 += t2*t2;
        }
        double std = Math.sqrt(t1);
        return std;
    }
    public double mean (ArrayList<Double> data){
        double sum = 0;
        for (double i:data){
            sum += i;
        }
        double mean = sum/data.size();
        return mean;
    }



    public void shuffle(ArrayList<String> a){
        for (int i=a.size()-1;i>0;i--){
            int j = (int)(Math.random()*i);
            String t = a.get(i);
            a.set(i,a.get(j));
             a.set(j,t);
        }
    }
    /****
     * 承伤量
     * <伤害量> -> 不考虑力量2
     *
     * 击杀数:修正值
     *
     *
     *
     *
     *
     *
     * 100
       100
     *
     * */
}
