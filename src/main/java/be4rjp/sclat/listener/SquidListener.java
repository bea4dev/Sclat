
package be4rjp.sclat.listener;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.DeathMgr;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Be4rJP
 */
public class SquidListener implements Listener{
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = (Player)event.getPlayer();
        SquidListenerMgr.CheckOnInk(player);
    }
    
    //@EventHandler
    public void onPlayerSwitchSlot(PlayerItemHeldEvent event){
        Player player = (Player)event.getPlayer();
        PlayerData data = DataMgr.getPlayerData(player);
        SquidListenerMgr.CheckOnInk(player);
        if(player.getInventory().getItem(event.getNewSlot()).getType().equals(Material.AIR)){
            data.setIsSquid(true);
            return;
        }
            data.setIsSquid(false);
            
        
    }
}
