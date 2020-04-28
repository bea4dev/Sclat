
package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Path {
    private Location from , to;
    private Team team = null;
    private ArmorStand as;
    
    public Path(Location from, Location to){this.from = from; this.to = to;}
    
    
    public ArmorStand getArmorStand(){return this.as;}
    
    public Location getFromLocation(){return this.from;}
    
    public Location getToLocation(){return this.to;}
    
    public Team getTeam(){return this.team;}
    
    public void setArmorStand(ArmorStand as){this.as = as;}
    
    
    
    public void setTeam(Team team){
        this.team = team;
        
        as.setHelmet(new ItemStack(team.getTeamColor().getGlass()));
    }
    
    
    
    public void stop(){
        as.remove();
    }
}
