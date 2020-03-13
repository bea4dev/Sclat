
package be4rjp.sclat.GUI;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.MatchMgr;
import org.bukkit.GameMode;
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
        if(name.equals("リソースパックをダウンロード / DOWNLOAD RESOUCEPACK"))
            player.setResourcePack("https://github.com/Be4rJP/Sclat/releases/download/0/Sclat.zip");
        if(event.getClickedInventory().getTitle().equals("武器選択")){
            DataMgr.getPlayerData(player).setWeaponClass(DataMgr.getWeaponClass(name));
            player.sendMessage(name + "を選択しました");
        }
        
        if(event.getClickedInventory().getTitle().equals("設定")){
            switch (name){
                case "シューターのパーティクル":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_Shooter();
                    break;
                case "チャージャーのレーザー":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_ChargerLine();
                    break;
                case "チャージャーの射撃エフェクト":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_ChargerShot();
                    break;
                case "ローラーのロール":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_RollerRoll();
                    break;
                case "ローラーのしぶき":
                    DataMgr.getPlayerData(player).getSettings().S_ShowEffect_RollerShot();
                    break;
            }
            
            OpenGUI.openSettingsUI(player);
            
            player.playNote(player.getLocation(), Instrument.STICKS, Note.flat(1, Note.Tone.C));
            
            String E_S = DataMgr.getPlayerData(player).getSettings().ShowEffect_Shooter() ? "1" : "0";
            String E_CL = DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerLine() ? "1" : "0";
            String E_CS = DataMgr.getPlayerData(player).getSettings().ShowEffect_ChargerShot() ? "1" : "0";
            String E_RR = DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerRoll() ? "1" : "0";
            String E_RS = DataMgr.getPlayerData(player).getSettings().ShowEffect_RollerShot() ? "1" : "0";
            String E_Sq = DataMgr.getPlayerData(player).getSettings().ShowEffect_Squid() ? "1" : "0";
            
            String s_data = E_S + E_CL + E_CS + E_RR + E_RS + E_Sq;
            
            String uuid = player.getUniqueId().toString();
            conf.getPlayerSetiings().set("Settings." + uuid, s_data);
        }
        
        if(!player.getGameMode().equals(GameMode.CREATIVE))
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
