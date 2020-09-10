
package be4rjp.sclat.weapon;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.weapon.spweapon.BombRush;
import be4rjp.sclat.weapon.spweapon.SuperArmor;
import be4rjp.sclat.weapon.spweapon.SuperSensor;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Be4rJP
 */
public class SPWeapon implements Listener{
    //スペシャルウエポンのリスナー部分
    @EventHandler
    public void onClickSPWeapon(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        PlayerData data = DataMgr.getPlayerData(player);
        
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))
                    SPWeaponMgr.UseSPWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
        
    }
}
