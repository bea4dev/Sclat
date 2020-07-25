
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.SPWeaponMgr;
import org.bukkit.Sound;
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
        data.setIsUsingSP(true);
        SPWeaponMgr.setSPCoolTimeAnimation(player, 120);
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                data.setIsBombRush(false);
                //player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 2);
                data.setIsUsingSP(false);
            }
        };
        task.runTaskLater(Main.getPlugin(), 120);
    }
}
