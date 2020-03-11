
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class DamageMgr {
    public static void SclatGiveDamage(Player player, double damage){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getArmor() <= 0)
            player.damage(damage);
        if(data.getArmor() > 0){
            data.setArmor(data.getArmor() - damage);
        }
    }
    
    public static void SclatGiveStrongDamage(Player target, double damage, Player player){
        PlayerData data = DataMgr.getPlayerData(target);
        if(data.getArmor() <= 0)
            if(target.getHealth() > damage){
                target.damage(damage);
                PaintMgr.Paint(target.getLocation(), player, true);
            }else{
                target.setGameMode(GameMode.SPECTATOR);
                DeathMgr.PlayerDeathRunnable(target, player, "killed");
                PaintMgr.Paint(target.getLocation(), player, true);
            }
        if(data.getArmor() > 0){
            data.setArmor(data.getArmor() - damage);
        }
    }

}
