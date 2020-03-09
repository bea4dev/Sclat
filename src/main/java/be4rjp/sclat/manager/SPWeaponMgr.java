
package be4rjp.sclat.manager;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    
    public static void SPWeaponRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            ItemStack is = null;
            ItemMeta ism = null;
            
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                if(data.getSPGauge() == 100 && p.getInventory().getItem(4).equals(Material.AIR)){
                    switch (data.getWeaponClass().getSPWeaponName()) {
                        case "スーパーアーマー":
                            is = new ItemStack(Material.TOTEM_OF_UNDYING);
                            ism = is.getItemMeta();
                            ism.setDisplayName("スーパーアーマー");
                            break;
                        case "ボムラッシュ":
                            is = new ItemStack(Material.DISPENSER);
                            ism = is.getItemMeta();
                            ism.setDisplayName("ボムラッシュ");
                            break;
                    }

                    is.setItemMeta(ism);
                    p.getInventory().setItem(4, is);  

                    if(!DataMgr.getPlayerData(p).isInMatch())
                        cancel();
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 20);
    }
}
