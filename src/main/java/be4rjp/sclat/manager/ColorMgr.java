package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.Color;
import be4rjp.sclat.data.DataMgr;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 *
 * @author Be4rJP
 */
public class ColorMgr {
    public synchronized static void SetupColor(){
        Color blue = new Color("Blue");
            blue.setWool(Material.BLUE_WOOL);
            blue.setConcrete(Material.BLUE_CONCRETE);
            blue.setGlass(Material.BLUE_STAINED_GLASS);
            blue.setColorCode("§9");
            blue.setBukkitColor(org.bukkit.Color.BLUE);
                ItemStack bh = new ItemStack(Material.LEATHER_HELMET, 1);
                LeatherArmorMeta bhm = (LeatherArmorMeta)bh.getItemMeta();
                bhm.setColor(org.bukkit.Color.BLUE);
                bh.setItemMeta(bhm);
            blue.setBougu(bh);
            blue.setChatColor(ChatColor.BLUE);
        if(Main.colors.size() == 0 || Main.colors.contains("Blue")) {
            DataMgr.setColor("Blue", blue);
            DataMgr.addColorList(blue);
        }
            
        Color aqua = new Color("Aqua");
            aqua.setWool(Material.LIGHT_BLUE_WOOL);
            aqua.setConcrete(Material.LIGHT_BLUE_CONCRETE);
            aqua.setGlass(Material.LIGHT_BLUE_STAINED_GLASS);
            aqua.setColorCode("§b");
            aqua.setBukkitColor(org.bukkit.Color.AQUA);
                ItemStack ah = new ItemStack(Material.LEATHER_HELMET, 1);
                LeatherArmorMeta ahm = (LeatherArmorMeta)ah.getItemMeta();
                ahm.setColor(org.bukkit.Color.AQUA);
                ah.setItemMeta(ahm);
            aqua.setBougu(ah);
            aqua.setChatColor(ChatColor.AQUA);
        if(Main.colors.size() == 0 || Main.colors.contains("Aqua")) {
            DataMgr.setColor("Aqua", aqua);
            DataMgr.addColorList(aqua);
        }
        
        Color orange = new Color("Orange");
            orange.setWool(Material.ORANGE_WOOL);
            orange.setConcrete(Material.ORANGE_CONCRETE);
            orange.setGlass(Material.ORANGE_STAINED_GLASS);
            orange.setColorCode("§6");
            orange.setBukkitColor(org.bukkit.Color.ORANGE);
                ItemStack oh = new ItemStack(Material.LEATHER_HELMET, 1);
                LeatherArmorMeta ohm = (LeatherArmorMeta)oh.getItemMeta();
                ohm.setColor(org.bukkit.Color.ORANGE);
                oh.setItemMeta(ohm);
            orange.setBougu(oh);
            orange.setChatColor(ChatColor.GOLD);
        if(Main.colors.size() == 0 || Main.colors.contains("Orange")) {
            DataMgr.setColor("Orange", orange);
            DataMgr.addColorList(orange);
        }
        
        Color lime = new Color("Lime");
            lime.setWool(Material.LIME_WOOL);
            lime.setConcrete(Material.LIME_CONCRETE);
            lime.setGlass(Material.LIME_STAINED_GLASS);
            lime.setColorCode("§a");
            lime.setBukkitColor(org.bukkit.Color.LIME);
                ItemStack lh = new ItemStack(Material.LEATHER_HELMET, 1);
                LeatherArmorMeta lhm = (LeatherArmorMeta)lh.getItemMeta();
                lhm.setColor(org.bukkit.Color.LIME);
                lh.setItemMeta(lhm);
            lime.setBougu(lh);
            lime.setChatColor(ChatColor.GREEN);
        if(Main.colors.size() == 0 || Main.colors.contains("Lime")) {
            DataMgr.setColor("Lime", lime);
            DataMgr.addColorList(lime);
        }
        
        Color y = new Color("Yellow");
            y.setWool(Material.YELLOW_WOOL);
            y.setConcrete(Material.YELLOW_CONCRETE);
            y.setGlass(Material.YELLOW_STAINED_GLASS);
            y.setColorCode("§e");
            y.setBukkitColor(org.bukkit.Color.YELLOW);
                ItemStack yh = new ItemStack(Material.LEATHER_HELMET, 1);
                LeatherArmorMeta yhm = (LeatherArmorMeta)yh.getItemMeta();
                yhm.setColor(org.bukkit.Color.YELLOW);
                yh.setItemMeta(yhm);
            y.setBougu(yh);
            y.setChatColor(ChatColor.YELLOW);
        if(Main.colors.size() == 0 || Main.colors.contains("Yellow")) {
            DataMgr.setColor("Yellow", y);
            DataMgr.addColorList(y);
        }
    }
}
