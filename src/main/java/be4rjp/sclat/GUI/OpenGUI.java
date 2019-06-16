
package be4rjp.sclat.GUI;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Be4rJP
 */
public class OpenGUI {
    public static void openMenu(Player player){
        Inventory inv = Bukkit.createInventory(null, 9, "メインメニュー");
        ItemStack join = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta joinmeta = join.getItemMeta();
        joinmeta.setDisplayName("試合に参加");
        join.setItemMeta(joinmeta);
        inv.setItem(2, join);
        ItemStack w = new ItemStack(Material.WOODEN_HOE);
        ItemMeta wmeta = w.getItemMeta();
        wmeta.setDisplayName("武器選択");
        w.setItemMeta(wmeta);
        inv.setItem(6, w);
        player.openInventory(inv);
    }
    public static void openWeaponSelect(Player player){
        int slotnum = 0;
        Inventory inv = Bukkit.createInventory(null, 54, "武器選択");
        for (String classname : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
            String ClassName = conf.getClassConfig().getString("WeaponClass." + classname + ".MainWeaponName");
            ItemStack item = new ItemStack(DataMgr.getWeaponClass(ClassName).getMainWeapon().getWeaponIteamStack());
            ItemMeta itemm = item.getItemMeta();
            itemm.setDisplayName(ClassName);
            List lores = new ArrayList();
            lores.add("");
            lores.add("");
            itemm.setLore(lores);
            item.setItemMeta(itemm);
            if (slotnum <= 53){
                inv.setItem(slotnum, item);
            }
            slotnum++;
        }
        player.openInventory(inv);
    }
}
