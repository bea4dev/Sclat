
package be4rjp.sclat.data;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;

/**
 *
 * @author Be4rJP
 */
public class Color {
    private String colorname;
    private String colorcode;
    private boolean isUsed = false;
    
    public Color(String colorname){this.colorname = colorname;}
    
    public String getColorCode(){return colorcode;}
    
    public boolean getIsUsed(){return isUsed;}
    
    public void setIsUsed(boolean isused){isUsed = isused;}
    
    
}
