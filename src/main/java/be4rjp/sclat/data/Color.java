
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
    private ChatColor colorcode;
    
    public Color(String colorname){this.colorname = colorname;}
    
    public ChatColor getColorCode(){return colorcode;}
    
    
}
