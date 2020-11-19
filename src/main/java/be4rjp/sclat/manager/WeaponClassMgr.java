
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.WeaponClass;
import org.bukkit.ChatColor;
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
        ItemStack main = data.getWeaponClass().getMainWeapon().getWeaponIteamStack().clone();
        if(data.getMainItemGlow()){
            Main.glow.enchantGlow(main);
            main.addEnchantment(Main.glow, 1);
        }
        player.getInventory().setItem(0, main);
        if(data.getWeaponClass().getMainWeapon().getIsManeuver())
            player.getInventory().setItem(40, data.getWeaponClass().getMainWeapon().getWeaponIteamStack().clone());
        ItemStack is = SubWeaponMgr.getSubWeapon(player);
        player.getInventory().setItem(2, is);
        ItemStack co = new ItemStack(Material.BOOK);
        ItemMeta meta = co.getItemMeta();
        meta.setDisplayName("スーパージャンプ");
        co.setItemMeta(meta);
        player.getInventory().setItem(6, co);
        if(!data.getIsSquid())
            player.getEquipment().setHelmet(DataMgr.getPlayerData(player).getTeam().getTeamColor().getBougu());
        
        if(data.getSPGauge() == 100)
            SPWeaponMgr.setSPWeapon(player);
        
        if(conf.getConfig().getString("WorkMode").equals("Trial") && !Main.tutorial){
            ItemStack join = new ItemStack(Material.CHEST);
            ItemMeta joinmeta = join.getItemMeta();
            joinmeta.setDisplayName(ChatColor.GOLD + "右クリックでメインメニューを開く");
            join.setItemMeta(joinmeta);
            player.getInventory().setItem(7, join);
        }
    }
    
}
