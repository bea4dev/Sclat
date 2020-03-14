
package be4rjp.sclat.data;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
    private Material concrete;
    private ItemStack bougu;
    
    public Color(String colorname){this.colorname = colorname;}
    
    public String getColorCode(){return colorcode;}
    
    public String getColorName(){return colorname;}
    
    public boolean getIsUsed(){return isUsed;}
    
    public Material getWool(){return wool;}
    
    public Material getGlass(){return glass;}
    
    public Material getConcrete(){return concrete;}
    
    public ItemStack getBougu(){return bougu;}
    
    public org.bukkit.Color getBukkitColor(){return bukkitcolor;}
    
    
    public void setWool(Material Wool){wool = Wool;}
    
    public void setGlass(Material glass){this.glass = glass;}
    
    public void setIsUsed(boolean isused){isUsed = isused;}
    
    public void setColorCode(String code){colorcode = code;}
    
    public void setConcrete(Material conc){this.concrete = conc;}
    
    public void setBougu(ItemStack bougu){this.bougu = bougu;}
    
    public void setBukkitColor(org.bukkit.Color color){this.bukkitcolor = color;}
    
    
}
