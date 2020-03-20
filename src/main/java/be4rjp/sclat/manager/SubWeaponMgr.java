
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.subweapon.KBomb;
import be4rjp.sclat.weapon.subweapon.Poison;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.Sensor;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Be4rJP
 */
public class SubWeaponMgr {
    public static ItemStack getSubWeapon(Player player){
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
            case "センサー":
                is = new ItemStack(Material.DISPENSER);
                ism = is.getItemMeta();
                ism.setDisplayName("センサー");
                break;
            case "ポイズン":
                is = new ItemStack(Material.PRISMARINE);
                ism = is.getItemMeta();
                ism.setDisplayName("ポイズン");
                break;
            case "キューバンボム":
                is = new ItemStack(data.getTeam().getTeamColor().getConcrete());
                ism = is.getItemMeta();
                ism.setDisplayName("キューバンボム");
                break;
        }
        is.setItemMeta(ism);
        //player.getInventory().setItem(2, is);  
        return is;
    }
    
    public static void UseSubWeapon(Player player, String name){
        PlayerData data = DataMgr.getPlayerData(player);
        switch (name) {
            case "スプラッシュボム":
                SplashBomb.SplashBomRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "クイックボム":
                QuickBomb.QuickBomRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "センサー":
                Sensor.SensorRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "ポイズン":
                Poison.PoisonRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
            case "キューバンボム":
                KBomb.KBomRunnable(player);
                data.setCanUseSubWeapon(false);
                break;
        }
    }
}
