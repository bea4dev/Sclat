
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
        if(data.getArmor() > 0){
            data.setArmor(data.getArmor() - damage);
        }
        if(data.getArmor() <= 0)
            player.damage(damage);
        
    }
}
