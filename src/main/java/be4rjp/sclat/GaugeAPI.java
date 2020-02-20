
package be4rjp.sclat;

import org.bukkit.ChatColor;

/**
 *
 * @author Be4rJP
 */
public class GaugeAPI {
    public static String toGauge(int value, int max, String color1, String color2){
        String m = "|";
        StringBuilder ms = new StringBuilder();
        ms.append(ChatColor.RESET + color1);
        for (int i = 1; i <= value; i++){
            ms.append(m);
        }
        ms.append(color2);
        int rem = max - value;
        for (int i1 = 1; i1 <= rem; i1++){
            ms.append(m);
        }
        ms.append(ChatColor.RESET);
        return ms.toString();
    }
}
