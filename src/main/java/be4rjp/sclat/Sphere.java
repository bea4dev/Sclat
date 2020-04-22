
package be4rjp.sclat;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;

/**
 *
 * @author Be4rJP
 */
public class Sphere {
    
    public static List<Location> getSphere(Location baseLoc, double r ,int accuracy){
        List<Location> tempList = new ArrayList<Location>();
        int count = 0;
        for(int i = 0; i < 180; i += accuracy){
            for(int t = 0; t < 360; t += accuracy){
                int s = 1;
                if(count % 2 == 0)
                    s = -1;
                double x = r * Math.cos(Math.toRadians(i)) * Math.cos(Math.toRadians(t)) * s;
                double y = r * Math.cos(Math.toRadians(i)) * Math.sin(Math.toRadians(t)) * s;
                double z = r * Math.sin(Math.toRadians(i)) * s;
                Location sphereLoc = new Location(baseLoc.getWorld(), baseLoc.getX() + x, baseLoc.getY() + y, baseLoc.getZ() + z);
                tempList.add(sphereLoc);
                count++;
            }
        }
        return tempList;
    }
    
    public static List<Location> getXZCircle(Location baseLoc, double r ,int accuracy){
        List<Location> tempList = new ArrayList<Location>();
        for(int tr = 1; tr <= r; tr++){
            for(int t = 0; t < 360; t += accuracy/tr){
                double x = tr * Math.sin(Math.toRadians(t));
                double z = tr * Math.cos(Math.toRadians(t));
                Location loc = new Location(baseLoc.getWorld(), baseLoc.getX() + x, baseLoc.getY(), baseLoc.getZ() + z);
                tempList.add(loc);
            }
        }
        return tempList;
    }
    
}
