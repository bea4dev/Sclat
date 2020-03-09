
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class BombRush {
    public static void BombRushRunnable(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        data.setIsBombRush(true);
        
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                data.setIsBombRush(false);
            }
        };
        task.runTaskLater(Main.getPlugin(), 140);
    }
}
