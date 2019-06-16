
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Be4rJP
 */
public class SubWeaponMgr {
    public static void setSubWeapon(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if("スプラッシュボム".equals(data.getWeaponClass().getSubWeaponName())){
            ItemStack is = new ItemStack(data.getTeam().getTeamColor().getGlass());
            ItemMeta ism = is.getItemMeta();
            ism.setDisplayName("スプラッシュボム");
            is.setItemMeta(ism);
            player.getInventory().setItem(2, is);
        }
            
    }
}
