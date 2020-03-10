
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
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
            if(DataMgr.getPlayerData(player).getTeam().getID() != DataMgr.getPlayerData(o_player).getTeam().getID())
                o_player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
        }
    }
}
