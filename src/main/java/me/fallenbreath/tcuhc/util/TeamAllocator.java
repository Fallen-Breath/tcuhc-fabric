package me.fallenbreath.tcuhc.util;

import org.lwjgl.system.CallbackI;

import java.util.*;

public class TeamAllocator {
    static double k_singleGame = 0.2; // pp更新常数 越大说明单局游戏的表现影响越高
    static double k_wStreak = 1.025; // 连胜影响系数 越大说明连胜/连败影响越大
    static double k_point_factor = 1.5; // 战力修正值, 越大的话说明战斗力质量越重要.
    public TeamAllocator(){

    }
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




    public ArrayList<ArrayList<String>> matchMaking(Map<String,Double> players,int teamNums){
        ArrayList<ArrayList<String>> res = new ArrayList();
        for (int i=0;i<teamNums;i++){
            res.add(new ArrayList<>());
        }
        LinkedHashMap<String,Double> sortedPlayers = new LinkedHashMap<>();
        players.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedPlayers.put(x.getKey(),x.getValue()));

        double[] scores = new double[teamNums];
        int expectedPlayerNum = (players.keySet().size()/teamNums)+1;
        for (String player: sortedPlayers.keySet()){ //从分数最高的玩家到分数最低的玩家
            int weekTeam = 0;
            for (int i=0;i<teamNums;i++){ //选择一个最弱的队,并且这个队没有满员
                if (scores[weekTeam]>scores[i]&&res.get(i).size()<=expectedPlayerNum){
                    weekTeam = i;
                }
            }
            res.get(weekTeam).add(player); //将这个玩家分配给最弱的队
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
    /**
     * 计算当前游戏技能分数等级
     * */
    public double SGPP (double personalData,double overallMean, double overallStd){
        double pp = (personalData - overallMean) / overallStd;
        return pp;

    }
    /**
     *  pp: 技能分数
     *  SGPP: 当前pp
     *  hisPP: 历史pp
     *  wStreak: 正为连胜 负为连败
     *  k: 影响系数
     * */
    public double updatePersonalPP (double hisPP, double SGPP, int wStreak){
         return (1-k_singleGame)*hisPP+(k_singleGame*SGPP*Math.pow(k_wStreak,(wStreak-1)));
    }
    /**
    * 更新所有玩家的技能分数
     *
     * 输入: <UUID,PP>,当前所有玩家的技能分数.
     * 输出: <UUID,PP>,更新以后的玩家技能分数.
     * public HashMap<String,Double> updateAllPP(HashMap<String,Double> hisPP){
     *
     *     }
    * */


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
