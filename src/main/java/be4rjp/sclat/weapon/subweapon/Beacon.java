
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Beacon {
    public static void setBeacon(Player player){
        if(player.isOnGround() && DataMgr.getPlayerData(player).isInMatch() && player.getExp() >= 0.4){
            ArmorStand as = DataMgr.getBeaconFromplayer(player);
            as.setVisible(false);
            as.setHelmet(new ItemStack(Material.AIR));
            as.teleport(player.getLocation().add(0, -0.45, 0));
            as.setCustomName("21");
            player.setExp(player.getExp() - 0.39F);
            BukkitRunnable delay = new BukkitRunnable(){
                @Override
                public void run(){
                    as.setHelmet(new ItemStack(Material.IRON_TRAPDOOR));
                    as.setVisible(true);
                }
            };
            delay.runTaskLater(Main.getPlugin(), 10);
        }else if(player.getExp() < 0.4)
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
        
        BukkitRunnable delay = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setCanUseSubWeapon(true);
            }
        };
        delay.runTaskLater(Main.getPlugin(), 20);
    }
}
