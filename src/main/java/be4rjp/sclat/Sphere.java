
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
    
    public static synchronized List<Location> getSphere(Location baseLoc, double r){
        List<Location> tempList = new ArrayList<Location>();
        for(int i = 0; i > 180; i++){
            int t = i * 2;
            double x = r * Math.cos(Math.toRadians(i)) * Math.cos(Math.toRadians(t));
            double y = r * Math.cos(Math.toRadians(i)) * Math.sin(Math.toRadians(t));
            double z = r * Math.sin(Math.toRadians(i));
            Location sphereLoc = new Location(baseLoc.getWorld(), baseLoc.getX() + x, baseLoc.getY() + y, baseLoc.getZ() + z);
            tempList.add(sphereLoc);
        }
        return tempList;
    }
}
