package be4rjp.sclat.manager;

import be4rjp.sclat.data.Color;
import be4rjp.sclat.data.DataMgr;
import org.bukkit.Material;

/**
 *
 * @author Be4rJP
 */
public class ColorMgr {
    public synchronized static void SetupColor(){
        Color blue = new Color("Blue");
            blue.setWool(Material.BLUE_WOOL);
            blue.setGlass(Material.BLUE_STAINED_GLASS);
            blue.setColorCode("§9");
            blue.setBukkitColor(org.bukkit.Color.BLUE);
        DataMgr.setColor("Blue", blue);
        DataMgr.addColorList(blue);
            
        Color aqua = new Color("Aqua");
            aqua.setWool(Material.LIGHT_BLUE_WOOL);
            aqua.setGlass(Material.LIGHT_BLUE_STAINED_GLASS);
            aqua.setColorCode("§b");
            aqua.setBukkitColor(org.bukkit.Color.AQUA);
        DataMgr.setColor("Aqua", aqua);
        DataMgr.addColorList(aqua);
        
        Color orange = new Color("Orange");
            orange.setWool(Material.ORANGE_WOOL);
            orange.setGlass(Material.ORANGE_STAINED_GLASS);
            orange.setColorCode("§6");
            orange.setBukkitColor(org.bukkit.Color.ORANGE);
        DataMgr.setColor("Orange", orange);
        DataMgr.addColorList(orange);
    }
}
