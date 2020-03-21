
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.WeaponClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Be4rJP
 */
public class WeaponClassMgr {
    public synchronized static void WeaponClassSetup(){
        for (String classname : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
            String WeaponName = conf.getClassConfig().getString("WeaponClass." + classname + ".MainWeaponName");
            String SubWeaponName = conf.getClassConfig().getString("WeaponClass." + classname + ".SubWeaponName");
            String SPWeaponName = conf.getClassConfig().getString("WeaponClass." + classname + ".SPWeaponName");
            WeaponClass wc = new WeaponClass(classname);
                wc.setMainWeapon(DataMgr.getWeapon(WeaponName));
                wc.setSubWeaponName(SubWeaponName);
                wc.setSPWeaponName(SPWeaponName);
            
            DataMgr.setWeaponClass(classname, wc);
        }
    }
    
    public static void setWeaponClass(Player player){
        player.getInventory().clear();
        PlayerData data = DataMgr.getPlayerData(player);
        player.getInventory().setItem(0, data.getWeaponClass().getMainWeapon().getWeaponIteamStack());
        ItemStack is = SubWeaponMgr.getSubWeapon(player);
        player.getInventory().setItem(2, is);
        if(conf.getConfig().getString("WorkMode").equals("Trial")){
            ItemStack join = new ItemStack(Material.CHEST);
            ItemMeta joinmeta = join.getItemMeta();
            joinmeta.setDisplayName("メインメニュー");
            join.setItemMeta(joinmeta);
            player.getInventory().setItem(7, join);
        }
    }
    
}
