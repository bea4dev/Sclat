
package be4rjp.sclat.GUI;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.MatchMgr;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Be4rJP
 */
public class ClickListener implements Listener{
    @EventHandler
    public void onGUIClick(InventoryClickEvent event){
        String name = event.getCurrentItem().getItemMeta().getDisplayName();
        Player player = (Player)event.getWhoClicked();
        player.closeInventory();
        //player.sendMessage(name);
        
        if("試合に参加".equals(name))
            MatchMgr.PlayerJoinMatch(player);
        if("武器選択".equals(name))
            OpenGUI.openWeaponSelect(player);
        if(event.getClickedInventory().getTitle().equals("武器選択")){
            DataMgr.getPlayerData(player).setWeaponClass(DataMgr.getWeaponClass(name));
            player.sendMessage(name + "を選択しました");
        }
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onOpenMainMenu(PlayerInteractEvent event){
        Player player = (Player)event.getPlayer();
        Action action = event.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            if(player.getInventory().getItemInMainHand().getType().equals(Material.CHEST))
                OpenGUI.openMenu(player);
        }
    }
}
