
package be4rjp.sclat;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Team;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Animation {
    public static void ResultAnimation(Player player, int team0point, int team1point, String team0color, String team1color, Team winteam, Boolean hikiwake){
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            int g = 0;
            @Override
            public void run(){
                
                if(i <= 15){
                    player.sendTitle("", String.valueOf(g) + "% [" + GaugeAPI.toGauge(g,50,team0color,"§7") + GaugeAPI.toGauge(50 - g,50,"§7",team1color) + "] " + String.valueOf(g) + "%", 0, 40, 0);
                    g = g + 2;
                }
                /*
                if(i >= 6 && i <= 10){
                    player.sendTitle("", String.valueOf(g) + "% [" + GaugeAPI.toGauge(g,50,team0color,"§7") + GaugeAPI.toGauge(50 - g,50,"§7",team1color) + "] " + String.valueOf(g) + "%", 0, 40, 0);
                    g++;
                }*/
                if(i == 35){
                    if(hikiwake){
                        player.sendTitle("引き分け！", "[" + GaugeAPI.toGauge(50,100, team0color, team1color) + "]", 0, 40, 10);
                    }else{
                        player.sendTitle(winteam.getTeamColor().getColorCode() + winteam.getTeamColor().getColorName() + "チーム" + ChatColor.RESET + "の勝利！", String.valueOf(team0point) + "% [" + GaugeAPI.toGauge(team0point,100, team0color, team1color) + "] " + String.valueOf(100 - team0point) + "%", 0, 40, 10);
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 13.0F, 1.5F);
                }
                if(i == 40){
                    if(winteam == DataMgr.getPlayerData(player).getTeam())
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
                    cancel();
                }
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 2);
    }
}
