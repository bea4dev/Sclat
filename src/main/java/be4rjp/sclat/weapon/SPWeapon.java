
package be4rjp.sclat.weapon;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.spweapon.SuperArmor;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Be4rJP
 */
public class SPWeapon {
    //スペシャルウエポンのリスナー部分
    @EventHandler
    public void onClickSPWeapon(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        PlayerData data = DataMgr.getPlayerData(player);
        
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            switch (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName()) {
                case "スーパーアーマー":
                    SuperArmor.setArmor(player, 20, 120);
                    break;
                case "ボムラッシュ":
                    
                    break;
            }
        }
    }
}