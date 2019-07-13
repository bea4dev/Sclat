
package be4rjp.sclat.weapon;

import be4rjp.sclat.GaugeAPI;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Roller {
    public static void HoldRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                
                if(data.getTick() <= 5 && data.isInMatch()){
                    data.setTick(data.getTick() + 1);
                }
                if(data.getTick() >= 6 && data.isInMatch()){
                    data.setTick(7);
                    data.setIsHolding(false);
                    data.setCanPaint(false);
                    data.setCanShoot(true);
                }
                
                if(!data.isInMatch())
                    cancel();
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void RollPaintRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(p);
                if(data.getIsHolding() && data.getCanPaint()){
                    if(player.getExp() <= data.getWeaponClass().getMainWeapon().getNeedInk()){
                        player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
                        return;
                    }
                    Vector loc = p.getEyeLocation().getDirection().normalize();
                    World w = p.getWorld();
                    PaintMgr.PaintHightestBlock(new Location(p.getWorld(), loc.toLocation(w).getX(), loc.toLocation(w).getY(), loc.toLocation(w).getZ()), p, true);
                    p.setWalkSpeed(0.1F);
                }else{
                    
                }
                
                if(!data.isInMatch())
                    cancel();
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 5);
    }
    
    public static void ShootPaintRunnable(Player player){
        PlayerData pdata = DataMgr.getPlayerData(player);
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            PlayerData data = pdata;
            @Override
            public void run(){
                p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1F, 1F);
                for (int i = 0; i < data.getWeaponClass().getMainWeapon().getRollerShootQuantity(); i++) {
                    Roller.Shoot(p);
                }
                data.setCanPaint(true);
                
            }
        
        };
        task.runTaskLater(Main.getPlugin(), pdata.getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void Shoot(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= data.getWeaponClass().getMainWeapon().getNeedInk()){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 13, 2);
            return;
        }
        player.setExp(player.getExp() - data.getWeaponClass().getMainWeapon().getNeedInk());
        Snowball ball = player.launchProjectile(Snowball.class);
        //player.playSound(player.getLocation(), Sound.ENTITY_PIG_STEP, 0.3F, 1F);
                Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
                double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
                int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
                if(player.isOnGround())
                    vec.add(new Vector(Math.random() * random - random/2, Math.random() * random / 4 - random/8, Math.random() * random - random/2));
                if(!player.isOnGround())
                    vec.add(new Vector(Math.random() * random / 4 - random/8, Math.random() * random - random, Math.random() * random / 4 - random/8));
                ball.setVelocity(vec);
                ball.setShooter(player);
                BukkitRunnable task = new BukkitRunnable(){
                    int i = 0;
                    int tick = distick;
                    //Vector fallvec;
                    Vector origvec = vec;
                    Snowball inkball = ball;
                    Player p = player;
                    Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
                    @Override
                    public void run(){
                        org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                        inkball.getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                        
                        if(i == tick)
                            inkball.setVelocity(fallvec);
                        if(i >= tick)
                            inkball.setVelocity(inkball.getVelocity().add(new Vector(0, -0.1, 0)));
                        if(i != tick)
                            PaintMgr.PaintHightestBlock(inkball.getLocation(), p, true);
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
