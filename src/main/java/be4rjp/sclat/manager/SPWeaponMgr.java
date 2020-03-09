
package be4rjp.sclat.manager;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SPWeaponMgr {
    
    public static void addSPCharge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() < 100)
            data.addSPGauge();
    }
    
    public static void resetSPCharge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() > 20)
            data.setSPGauge(20);
    }
    
    public static String getSPGauge(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.getSPGauge() == 100)
            return "§b§n! READY !";
        return GaugeAPI.toGauge(data.getSPGauge() / 5, 20, "§a", "§7");
    }
}
