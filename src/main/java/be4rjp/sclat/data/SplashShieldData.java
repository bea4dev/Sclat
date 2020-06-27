
package be4rjp.sclat.data;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SplashShieldData {
    private BukkitRunnable task;
    private List<ArmorStand> list = new ArrayList<ArmorStand>();
    private Player player;
    private double damage;
    
    public SplashShieldData(Player player){this.player = player;};
    
    public BukkitRunnable getTask(){return this.task;}
    
    public List<ArmorStand> getArmorStandList(){return this.list;}
    
    public Player getPlayer(){return this.player;}
    
    public double getDamage(){return this.damage;}
    
    
    public void setTask(BukkitRunnable task){this.task = task;}
    
    public void setArmorStandList(List<ArmorStand> list){this.list = list;}
    
    public void setDamage(double damage){this.damage = damage;}
}
