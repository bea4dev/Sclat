
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.Path;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
public class PathMgr {
    public static void setPath(Player player, Location from, Location to){
        BukkitRunnable path = new BukkitRunnable(){
            Player p = player;
            Item drop;
            int c = 0;
            Vector vec;
            @Override
            public void run(){
                if(c == 0){
                    drop = p.getWorld().dropItem(from.clone().add(0, -0.25, 0), new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool()));
                    drop.setGravity(false);
                    drop.setCustomName(String.valueOf(Main.getNotDuplicateNumber()));
                    //vec = (to.subtract(from)).toVector().normalize();
                    vec = new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()).normalize().multiply(0.5);
                    drop.addPassenger(p);
                    DataMgr.getPlayerData(p).setIsOnPath(true);
                }
                
                drop.setVelocity(vec);
                
                if(drop.getLocation().distance(from) > from.distance(to) || !drop.getPassengers().contains(p) || !DataMgr.getPlayerData(p).isInMatch() || !p.getInventory().getItemInMainHand().getType().equals(Material.AIR)){
                    DataMgr.getPlayerData(p).setIsOnPath(false);
                    drop.remove();
                    cancel();
                }
                    
                c++;
            }
        };
        path.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void setupPath(Match m){
        for(Path path : m.getMapData().getPathList()){
            Location from = path.getFromLocation().clone();
            
            BukkitRunnable ast = new BukkitRunnable(){
                @Override
                public void run(){
                    ArmorStand as = (ArmorStand)from.getWorld().spawnEntity(from.clone().add(0, -0.9, 0), EntityType.ARMOR_STAND);
                    as.setGlowing(false);
                    as.setGravity(false);
                    as.setVisible(false);
                    as.setSmall(true);
                    as.setCustomName("Path");
                    as.setCustomNameVisible(false);
                    as.setHelmet(new ItemStack(Material.WHITE_STAINED_GLASS));
                    DataMgr.addPathArmorStandList(as);
                    path.setArmorStand(as);
                }
            };
            ast.runTaskLater(Main.getPlugin(), 1);
            

            BukkitRunnable effect = new BukkitRunnable(){
                Path path1 = path;
                Location from = path.getFromLocation().clone();
                Location to = path.getToLocation().clone();
                Match match = m;
                @Override
                public void run(){
                    Team team = path1.getTeam();
                    RayTrace rayTrace = new RayTrace(from.toVector(), new Vector(to.getX() - from.getX(), to.getY() - from.getY(), to.getZ() - from.getZ()).normalize());
                    ArrayList<Vector> positions = rayTrace.traverse(from.distance(to), 0.5);
                    for(int i = 0; i < positions.size();i++){
                        Location position = positions.get(i).toLocation(from.getWorld());
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).getSettings().ShowEffect_ChargerLine())
                                continue;
                            if(team == null){
                                Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.WHITE, 1);
                                target.spawnParticle(Particle.REDSTONE, position, 1, 0, 0, 0, 25, dustOptions);
                            }else{
                                Particle.DustOptions dustOptions = new Particle.DustOptions(team.getTeamColor().getBukkitColor(), 1);
                                target.spawnParticle(Particle.REDSTONE, position, 1, 0, 0, 0, 25, dustOptions);
                            }
                        }
                    }
                    if(match.isFinished())
                        cancel();
                }
            };
            effect.runTaskTimer(Main.getPlugin(), 0, 5);

            BukkitRunnable task = new BukkitRunnable(){
                Path path1 = path;
                Location from = path.getFromLocation().clone();
                Location to = path.getToLocation().clone();
                Match match = m;
                @Override
                public void run(){
                    Team team = path1.getTeam();
                    for (Player player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(team != null){
                            if(DataMgr.getPlayerData(player).isInMatch() && player.getWorld() == from.getWorld() && player.getInventory().getItemInMainHand().getType().equals(Material.AIR) && DataMgr.getPlayerData(player).getTeam() == team && !DataMgr.getPlayerData(player).getIsOnPath()){
                                if(player.getLocation().distance(from) < 1)
                                    setPath(player, from, to);
                            }
                        }
                    }
                    if(match.isFinished())
                        cancel();
                }
            };
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        }
    }
}
