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
        DataMgr.setColor("Blue", blue);
            
        Color aqua = new Color("Aqua");
            aqua.setWool(Material.LIGHT_BLUE_WOOL);
            aqua.setColorCode("§b");
        DataMgr.setColor("Aqua", aqua);
    }
}
