
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class DamageMgr {
    public static void SclatGiveDamage(Player player, double damage){
        PlayerData data = DataMgr.getPlayerData(player);
        double armor = data.getArmor();
        if(armor > 0){
            if(armor < damage){
                player.damage(damage - armor);
                data.setArmor(0);
            }
            if(damage < armor)
                data.setArmor(data.getArmor() - damage);
        }
        if(armor <= 0)
            player.damage(damage);
        
    }
}
