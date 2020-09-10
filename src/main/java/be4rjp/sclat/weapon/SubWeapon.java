
package be4rjp.sclat.weapon;


import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.SubWeaponMgr;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;


/**
 *
 * @author Be4rJP
 */
public class SubWeapon implements Listener{
    //サブウエポンのリスナー部分
    @EventHandler
    public void onClickSubWeapon(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        PlayerData data = DataMgr.getPlayerData(player);
        
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            SubWeaponMgr.UseSubWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
        }
    }
    
    @EventHandler
    public void onPlayerClick(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        if(event.getAnimationType() == PlayerAnimationType.ARM_SWING){
            if(DataMgr.getPlayerData(player).isInMatch())
                SubWeaponMgr.UseSubWeapon(player, DataMgr.getPlayerData(player).getWeaponClass().getSubWeaponName());
        }
    }
}
