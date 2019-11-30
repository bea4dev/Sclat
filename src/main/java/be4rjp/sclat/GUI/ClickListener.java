
package be4rjp.sclat.GUI;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.MatchMgr;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
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
        
        if(name.equals("試合に参加 / JOIN THE MATCH"))
            MatchMgr.PlayerJoinMatch(player);
        if(name.equals("武器選択 / CHOSE WEAPONS"))
            OpenGUI.openWeaponSelect(player);
        if(name.equals("設定 / SETTINGS"))
            OpenGUI.openSettingsUI(player);
        if(event.getClickedInventory().getTitle().equals("武器選択")){
            DataMgr.getPlayerData(player).setWeaponClass(DataMgr.getWeaponClass(name));
            player.sendMessage(name + "を選択しました");
        }
        
        if(event.getClickedInventory().getTitle().equals("設定")){
            if(name.equals("シューターのパーティクル")){
                DataMgr.getPlayerData(player).getSettings().S_ShowEffect_Sooter();
                OpenGUI.openSettingsUI(player);
                player.playNote(player.getLocation(), Instrument.STICKS, Note.flat(1, Note.Tone.C));
            }
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
