
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
    
    public static List<String> ranking = new ArrayList<>();

    //レートを500単位で区切ってランク付けする
    public static String toABCRank(int ir){
        int MaxRate = (ranks.length - 1) * 500;
        return ir >= 0 ? ranks[ir <= MaxRate ? ir / 500 : ranks.length - 1] : "UnRanked";
    }
    
    public static void makeRankingAsync(){
        BukkitRunnable async = new BukkitRunnable() {
            @Override
            public void run() {
                //かぶらないようにマッピング
                Map<Integer, String> playerMap = new HashMap<>();
                for (String uuid : conf.getPlayerStatus().getConfigurationSection("Status").getKeys(false)){
                    int rate = conf.getPlayerStatus().getInt("Status." + uuid + ".Rank");
                    while (playerMap.containsKey(rate)){
                        rate++;
                    }
                    playerMap.put(rate, uuid);
                }
    
                Map<Integer, String> treeMap = new TreeMap<>(Comparator.reverseOrder());
                treeMap.putAll(playerMap);
                ranking = new ArrayList<>();
                for (Integer key : treeMap.keySet())
                    ranking.add(treeMap.get(key));
            }
        };
        async.runTaskAsynchronously(Main.getPlugin());
    }
    
    public static void makeRankingTask(){
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                makeRankingAsync();
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
