
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static be4rjp.sclat.Main.conf;
import static be4rjp.sclat.Main.type;

/**
 *
 * @author Be4rJP
 */
public class RankMgr {

    private static final String[] ranks = {"C-", "C", "C+", "B-", "B",
                                        "B+", "A-", "A", "A+", "S", "S+"};
    private static final int MAX_RATE = (ranks.length - 1) * 500;
    
    public static List<String> ranking = new ArrayList<>();
    public static List<String> killRanking = new ArrayList<>();
    public static List<String> paintRanking = new ArrayList<>();

    //レートを500単位で区切ってランク付けする
    public static String toABCRank(int ir){
        return ir >= 0 ? ranks[ir <= MAX_RATE ? ir / 500 : ranks.length - 1] : "UnRanked";
    }
    
    public static void addPlayerRankPoint(String uuid, int rankPoint){
        if(rankPoint == 0) return;
        
        int rank = PlayerStatusMgr.getRank(uuid);
    
        int MAX_RATE = ranks.length * 500;
        
        if(rank >= MAX_RATE) {
            if(rankPoint < 0){
                double minusRate = (double)MAX_RATE / ((double)MAX_RATE - (double)rank);
                int minus = (int)((double)rankPoint * minusRate);
                PlayerStatusMgr.addRank(uuid, -minus);
            }
            return;
        }
        
        if(rankPoint > 0){
            double plusRate = ((double)MAX_RATE - (double)rank) / (double)MAX_RATE;
            int plus = (int)((double)rankPoint * plusRate);
            PlayerStatusMgr.addRank(uuid, plus);
        }else{
            double minusRate = (double)MAX_RATE / ((double)MAX_RATE - (double)rank);
            int minus = (int)((double)rankPoint * minusRate);
            PlayerStatusMgr.addRank(uuid, minus);
        }
    }
    
    public static void makeRankingAsync(){
        BukkitRunnable async = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //かぶらないようにマッピング
                    Map<Integer, String> playerMap = new HashMap<>();
                    for (String uuid : conf.getPlayerStatus().getConfigurationSection("Status").getKeys(false)) {
                        int rate = conf.getPlayerStatus().getInt("Status." + uuid + ".Rank");
                        if(rate == 0) continue;
                        
                        while (playerMap.containsKey(rate)) {
                            rate++;
                        }
                        playerMap.put(rate, uuid);
                    }
    
                    Map<Integer, String> treeMap = new TreeMap<>(Comparator.reverseOrder());
                    treeMap.putAll(playerMap);
                    ranking = new ArrayList<>();
                    for (Integer key : treeMap.keySet())
                        ranking.add(treeMap.get(key));
                }catch (Exception e){}
            }
        };
        async.runTaskAsynchronously(Main.getPlugin());
    }
    
    public static void makeKillRankingAsync(){
        BukkitRunnable async = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //かぶらないようにマッピング
                    Map<Integer, String> playerMap = new HashMap<>();
                    for (String uuid : conf.getPlayerStatus().getConfigurationSection("Status").getKeys(false)) {
                        int rate = conf.getPlayerStatus().getInt("Status." + uuid + ".Kill");
                        if(rate == 0) continue;
                        
                        while (playerMap.containsKey(rate)) {
                            rate++;
                        }
                        playerMap.put(rate, uuid);
                    }
    
                    Map<Integer, String> treeMap = new TreeMap<>(Comparator.reverseOrder());
                    treeMap.putAll(playerMap);
                    killRanking = new ArrayList<>();
                    for (Integer key : treeMap.keySet())
                        killRanking.add(treeMap.get(key));
                }catch (Exception e){e.printStackTrace();}
            }
        };
        async.runTaskAsynchronously(Main.getPlugin());
    }
    
    public static void makePaintRankingAsync(){
        BukkitRunnable async = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    //かぶらないようにマッピング
                    Map<Integer, String> playerMap = new HashMap<>();
                    for (String uuid : conf.getPlayerStatus().getConfigurationSection("Status").getKeys(false)) {
                        int rate = conf.getPlayerStatus().getInt("Status." + uuid + ".Paint");
                        if(rate == 0) continue;
                        
                        while (playerMap.containsKey(rate)) {
                            rate++;
                        }
                        playerMap.put(rate, uuid);
                    }
    
                    Map<Integer, String> treeMap = new TreeMap<>(Comparator.reverseOrder());
                    treeMap.putAll(playerMap);
                    paintRanking = new ArrayList<>();
                    for (Integer key : treeMap.keySet())
                        paintRanking.add(treeMap.get(key));
                }catch (Exception e){}
            }
        };
        async.runTaskAsynchronously(Main.getPlugin());
    }
    
    public static void makeRankingTask(){
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                makeRankingAsync();
                makeKillRankingAsync();
                makePaintRankingAsync();
                for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
                    try {
                        DataMgr.getRankingHolograms(player).refreshRankingAsync();
                    }catch(Exception e){}
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, conf.getConfig().getInt("MakeRankingPeriod"));
    }
}
