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
            blue.setColorCode("§9");
            blue.setBukkitColor(org.bukkit.Color.BLUE);
        DataMgr.setColor("Blue", blue);
            
        Color aqua = new Color("Aqua");
            aqua.setWool(Material.LIGHT_BLUE_WOOL);
            aqua.setColorCode("§b");
            aqua.setBukkitColor(org.bukkit.Color.AQUA);
        DataMgr.setColor("Aqua", aqua);
        
        Color orange = new Color("Orange");
            aqua.setWool(Material.ORANGE_WOOL);
            aqua.setColorCode("§b");
            aqua.setBukkitColor(org.bukkit.Color.ORANGE);
        DataMgr.setColor("Orange", orange);
    }
}
