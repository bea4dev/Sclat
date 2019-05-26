
package be4rjp.sclat.weapon;

import be4rjp.sclat.data.DataMgr;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
                Snowball ball = player.launchProjectile(Snowball.class);
                Vector vec = player.getLocation().getDirection().multiply(4.0);
                vec.add(new Vector(Math.random() * 2 - 1, 0, Math.random() * 2 - 1));
                ball.setVelocity(vec);
                
            }
        }
    }
    public double random(){
        return Math.random() * 2 - 1;
    }
}
