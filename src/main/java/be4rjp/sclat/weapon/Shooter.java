
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.PaintMgr;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Shooter {
    public static void ShooterRunnable(Player player){
        BukkitRunnable delay = new BukkitRunnable(){
            Player p = player;
                            
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                //data.setTick(data.getTick() + DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootTick());
                if(data.getTick() < 5){
                    Shooter.Shoot(p);
                    data.setTick(data.getTick() + DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootTick());
                }
            }
        };
        delay.runTaskTimer(Main.getPlugin(), 0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void Shoot(Player player){
        Snowball ball = player.launchProjectile(Snowball.class);
                Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
                double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
                int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
                vec.add(new Vector(Math.random() * random - random/2, 0, Math.random() * random - random/2));
                ball.setVelocity(vec);
                ball.setShooter(player);
                BukkitRunnable task = new BukkitRunnable(){
                    int i = 0;
                    int tick = distick;
                    //Vector fallvec;
                    Vector origvec = vec;
                    Snowball inkball = ball;
                    Player p = player;
                    Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(0.2);
                    @Override
                    public void run(){
                        i++;
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                        inkball.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                        if(i <= tick){
                            Random random = new Random();
                            int r = random.nextInt(3);
                            if(r == 0){
                                Location loc = inkball.getLocation();
                                //PaintMgr.Paint(loc.getWorld().getHighestBlockAt(loc).getLocation(), p);
                            }
                        }
                        if(i == tick)
                            inkball.setVelocity(fallvec);                        
                        if(inkball.isDead())
                            cancel();
                    }
                };
                task.runTaskTimer(Main.getPlugin(), 0, 1);
                BukkitRunnable delay = new BukkitRunnable(){
                    Player p = player;
                    @Override
                    public void run(){
                        //p.getInventory().setItem(0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        //DataMgr.getPlayerData(p).setCanShoot(true);
                    }
                };
                delay.runTaskLater(Main.getPlugin(), DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
    }
}
