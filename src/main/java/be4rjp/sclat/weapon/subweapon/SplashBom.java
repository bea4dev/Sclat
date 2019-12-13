
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
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
                    Sphere.getSphere(drop.getLocation(), 4);
                    
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SplashBom()){
                                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                                o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, drop.getLocation(), 1, 0, 0, 0, 1, bd);
                            }       
                        }
                }
                
                
                
                c++;
                gc++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
                
    }
}
