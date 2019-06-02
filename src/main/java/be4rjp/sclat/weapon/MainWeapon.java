
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class MainWeapon implements Listener{
    @EventHandler
    public void onClickWeapon(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName())){
                //e.setCancelled(true);
                DataMgr.getPlayerData(player).setCanShoot(true);
                
                BukkitRunnable delay = new BukkitRunnable(){
                    Player p = player;
                    @Override
                    public void run(){
                        //p.getInventory().setItem(0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        DataMgr.getPlayerData(p).setCanShoot(false);
                    }
                };
                delay.runTaskLater(Main.getPlugin(), 2);
                int tick = 1;
		if(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick() < 5)
			tick=tick+(5-DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
                Shooter.ShooterRunnable(player);
                DataMgr.getPlayerData(player).setTick(tick);
            }
        }
    }
   
}
