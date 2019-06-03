
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.Bukkit;
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
    public static void ShooterRunnable(Player p){
        BukkitRunnable task = new BukkitRunnable(){
            Player player = p;
            //int firetick = tick;
            @Override
            public void run(){
                if(DataMgr.getPlayerData(player).getCanShoot() != true)
                    return;
                PlayerData data = DataMgr.getPlayerData(player);
                int tick = data.getTick();
		if(tick > 0) {
			tick--;
			data.setTick(tick);
			Shoot(player);
		} 
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void Shoot(Player player){
        Snowball ball = player.launchProjectile(Snowball.class);
                Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
                double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
                int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
                vec.add(new Vector(Math.random() * random - random/2, 0, Math.random() * random - random/2));
                ball.setVelocity(vec);
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
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                        inkball.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                        
                        if(i == tick)
                            inkball.setVelocity(fallvec);                        
                        if(inkball.isDead())
                            cancel();
                        i++;
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
