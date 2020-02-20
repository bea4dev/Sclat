
package be4rjp.sclat.data;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 *
 * @author Be4rJP
 */
public class Color {
    private String colorname;
    private String colorcode;
    private boolean isUsed = false;
    private Material wool;
    private org.bukkit.Color bukkitcolor;
    private Material glass;
    
    public Color(String colorname){this.colorname = colorname;}
    
    public String getColorCode(){return colorcode;}
    
    public String getColorName(){return colorname;}
    
    public boolean getIsUsed(){return isUsed;}
    
    public Material getWool(){return wool;}
    
    public Material getGlass(){return glass;}
    
    public org.bukkit.Color getBukkitColor(){return bukkitcolor;}
    
    
    public void setWool(Material Wool){wool = Wool;}
    
    public void setGlass(Material glass){this.glass = glass;}
    
    public void setIsUsed(boolean isused){isUsed = isused;}
    
    public void setColorCode(String code){colorcode = code;}
    
    public void setBukkitColor(org.bukkit.Color color){this.bukkitcolor = color;}
    
    
}
