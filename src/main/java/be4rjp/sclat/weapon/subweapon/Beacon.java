
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
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
        if(player.isOnGround() && DataMgr.getPlayerData(player).isInMatch() && player.getExp() > 0.4){
            ArmorStand as = DataMgr.getBeaconFromplayer(player);
            as.teleport(player.getLocation().add(0, -0.6, 0));
            as.setVisible(true);
            as.setHelmet(new ItemStack(Material.IRON_TRAPDOOR));
            as.setCustomName("21");
            player.setExp(player.getExp() - 0.39F);
        }
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
