
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
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
        //player.sendMessage(data.getWeaponClass().getSubWeaponName());
        ItemStack is = null;
        ItemMeta ism = null;
        
        switch (data.getWeaponClass().getSubWeaponName()) {
            case "スプラッシュボム":
                is = new ItemStack(data.getTeam().getTeamColor().getGlass());
                ism = is.getItemMeta();
                ism.setDisplayName("スプラッシュボム");
                break;
            case "クイックボム":
                is = new ItemStack(data.getTeam().getTeamColor().getWool());
                ism = is.getItemMeta();
                ism.setDisplayName("クイックボム");
                break;
        }
        is.setItemMeta(ism);
        player.getInventory().setItem(2, is);  
    }
}
