
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SuperSensor {
    public static void SuperSensorRunnable(Player player){
        for(Player o_player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(o_player).getTeam() && DataMgr.getPlayerData(o_player).isInMatch())
                o_player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
        }
        BukkitRunnable sound = new BukkitRunnable(){
            @Override
            public void run(){
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 2);
            }
        };
        sound.runTaskLater(Main.getPlugin(), 200);
    }
}
