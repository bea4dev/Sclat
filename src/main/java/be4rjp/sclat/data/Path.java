
package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import net.minecraft.server.v1_14_R1.EnumItemSlot;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEquipment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
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
    private ArmorStand as = null;
    private boolean setTeamed = false;
    
    public Path(Location from, Location to){this.from = from; this.to = to;}
    
    
    public ArmorStand getArmorStand(){return this.as;}
    
    public Location getFromLocation(){return this.from;}
    
    public Location getToLocation(){return this.to;}
    
    public Team getTeam(){return this.team;}
    
    public void setArmorStand(ArmorStand as){this.as = as;}
    
    
    
    public void setTeam(Team t){
        this.team = t;
        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
            if(as.getWorld() != target.getWorld())
                continue;
            if(t == null)
                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(Material.WHITE_STAINED_GLASS))));
            else
                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(as.getEntityId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(new ItemStack(team.getTeamColor().getGlass()))));
        }
        if(t != null)
            as.getWorld().playSound(as.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1F, 1F);
        else
            return;
        
        
        
        if(!setTeamed){
            setTeamed = true;
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    team = null;
                    setTeamed = false;
                }
            };
            task.runTaskLater(Main.getPlugin(), 400);
        }
    }
    
    
    
    public void stop(){
        as.remove();
    }
    
    public void reset(){
        this.team = null;
        this.as = null;
    }
}
