
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
public class SuperArmor {
    public static void setArmor(Player player, double armor, long delay, boolean effect){
        
        PlayerData data = DataMgr.getPlayerData(player);
        data.setArmor(armor);
        
        //エフェクト
        BukkitRunnable effect_r = new BukkitRunnable(){
            @Override
            public void run(){
                
            }
        };
        if(effect)
            effect_r.runTaskTimer(Main.getPlugin(), 0, 1);
        
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                data.setArmor(0);
            }
        };
        task.runTaskLater(Main.getPlugin(), delay);
        
    }  
}
