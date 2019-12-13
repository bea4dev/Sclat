
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SplashBom {
    
    public static void SplashBomRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            int gc = 0;
            Item drop;
            @Override
            public void run(){
                if(c == 0){
                    ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getGlass());
                    drop = p.getWorld().dropItem(p.getLocation(), bom);
                    drop.setVelocity(p.getEyeLocation().getDirection());
                }
                
                if(gc == 40){
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(target.equals(p) || !DataMgr.getPlayerData(target).getSettings().ShowEffect_SplashBom() || DataMgr.getPlayerData(p).getTeam().equals(DataMgr.getPlayerData(target).getTeam()))
                            continue;
                        
                            
                    }
                }
                
                Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                drop.getWorld().spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                
                c++;
                gc++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
                
    }
}
