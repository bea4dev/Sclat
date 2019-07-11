
package be4rjp.sclat.weapon;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Roller {
    public static void RollerRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int charge = 0;
            int max = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getMaxCharge();
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                
                if(data.getTick() <= 5 && data.isInMatch()){
                    data.setTick(data.getTick() + 1);
                    if(charge < max)
                        charge++;
                    
                }
                if(data.getTick() == 6 && data.isInMatch()){
                    if(p.getExp() > data.getWeaponClass().getMainWeapon().getNeedInk() * charge){
                        p.setExp(p.getExp() - data.getWeaponClass().getMainWeapon().getNeedInk() * charge);
                        Charger.Shoot(p, charge / 2, data.getWeaponClass().getMainWeapon().getDamage() * charge);
                    }else{
                        player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 10, 2);
                    }
                    charge = 0;
                    p.getInventory().setItem(0, data.getWeaponClass().getMainWeapon().getWeaponIteamStack());
                    data.setTick(7);
                    data.setIsHolding(false);
                }
                
                if(!data.isInMatch())
                    cancel();
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
