package be4rjp.sclat.data;

import java.util.Collections;
import java.util.List;
import org.bukkit.Location;

/**
 *
 * @author Be4rJP
 */
public class TeamLoc {
    private MapData map;
    
    private List<Location> list0;
    
    private List<Location> list1;

    
    public TeamLoc(MapData map){this.map = map;}
    
    public MapData getMap(){return this.map;}
    
    public void SetupTeam0Loc(){
        Location l = map.getTeam0Loc();
        //l.setX(l.getBlockX() + 0.5D);
        //l.setZ(l.getBlockZ() + 0.5D);
        //Location l1 = new Location(l.getWorld(), l.getBlockX() + 1D, l.getBlockY(), l.getBlockZ() + 1D);
        //Location l2 = new Location(l.getWorld(), l.getBlockX() - 1D, l.getBlockY(), l.getBlockZ() + 1D);
        //Location l3 = new Location(l.getWorld(), l.getBlockX() + 1D, l.getBlockY(), l.getBlockZ() - 1D);
        //Location l4 = new Location(l.getWorld(), l.getBlockX() - 1D, l.getBlockY(), l.getBlockZ() - 1D);
        //list0.add(l1);
        //list0.add(l2);
        //list0.add(l3);
        list0.add(l);
        Collections.shuffle(list0);
    }
    
    public void SetupTeam1Loc(){
        Location l = map.getTeam1Loc();
        l.setX(l.getBlockX() + 0.5D);
        l.setZ(l.getBlockZ() + 0.5D);
        Location l1 = new Location(l.getWorld(), l.getBlockX() + 1D, l.getBlockY(), l.getBlockZ() + 1D);
        Location l2 = new Location(l.getWorld(), l.getBlockX() - 1D, l.getBlockY(), l.getBlockZ() + 1D);
        Location l3 = new Location(l.getWorld(), l.getBlockX() + 1D, l.getBlockY(), l.getBlockZ() - 1D);
        Location l4 = new Location(l.getWorld(), l.getBlockX() - 1D, l.getBlockY(), l.getBlockZ() - 1D);
        list1.add(l1);
        list1.add(l2);
        list1.add(l3);
        list1.add(l4);
        Collections.shuffle(list1);
    }
    
    public Location getTeam0Loc(int i){return list0.get(i);}
    
    public Location getTeam1Loc(int i){return list1.get(i);}
    
}
