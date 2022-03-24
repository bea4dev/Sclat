
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;

import static be4rjp.sclat.Main.conf;
import static be4rjp.sclat.weapon.Blaster.Shoot;

import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.raytrace.RayTrace;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Be4rJP
 */
public class Burst {
    public static void BurstCooltime(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        BukkitRunnable delay1 = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setCanRollerShoot(true);
            }
        };
        if(data.getCanRollerShoot())
            delay1.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getCoolTime());
        
        BukkitRunnable delay = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                Burstshoot(player);
            }
        };
        if(data.getCanRollerShoot()){
            delay.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getDelay());
            data.setCanRollerShoot(false);
        }
    }
    
    public static void Burstshoot(Player player){
        String otoNumber = String.valueOf(Main.getNotDuplicateNumber());
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                c++;
                int q = DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getRollerShootQuantity();
                Shoot(p, q == 3 && DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootTick() == 2, otoNumber);
                if(c == q)
                    cancel();
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
    }
    
    public static void Shoot(Player player, boolean sound, String otoNumber){
        
        if(player.getGameMode() == GameMode.SPECTATOR) return;
        
        PlayerData data = DataMgr.getPlayerData(player);
        if(player.getExp() <= (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP))){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
            return;
        }
        player.setExp(player.getExp() - (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP)));
        
        PaintMgr.PaintHightestBlock(player.getLocation(), player, true, true);
        
        Snowball ball = player.launchProjectile(Snowball.class);
        ((CraftSnowball)ball).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool())));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIG_STEP, 0.3F, 1F);
        Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
        double random = data.getWeaponClass().getMainWeapon().getRandom();
        int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
        vec.add(new Vector(Math.random() * random - random/2, 0, Math.random() * random - random/2));
        ball.setVelocity(vec);
        ball.setShooter(player);
        String name = String.valueOf(Main.getNotDuplicateNumber()) + ":Burst:" + otoNumber;
        DataMgr.mws.add(name);
        if(sound)
            DataMgr.tsl.add(name);
        ball.setCustomName(name);
        DataMgr.getMainSnowballNameMap().put(name, ball);
        DataMgr.setSnowballHitCount(name, 0);
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            int tick = distick;
            //Vector fallvec;
            Vector origvec = vec;
            Snowball inkball = ball;
            boolean addedFallVec = false;
            Player p = player;
            Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
            @Override
            public void run(){
                inkball = DataMgr.getMainSnowballNameMap().get(name);
                
                if(!inkball.equals(ball)){
                    i+=DataMgr.getSnowballHitCount(name) - 1;
                    DataMgr.setSnowballHitCount(name, 0);
                }
    
                if(i != 0) {
                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if (DataMgr.getPlayerData(o_player).getSettings().ShowEffect_MainWeaponInk())
                            if (o_player.getWorld() == inkball.getWorld())
                                if (o_player.getLocation().distanceSquared(inkball.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                    o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 0, 0, -1, 0, 1, bd);
                    }
                }
                
                if(i >= tick && !addedFallVec){
                    inkball.setVelocity(fallvec);
                    addedFallVec = true;
                }
                if(i >= tick && i <= tick + 15)
                    inkball.setVelocity(inkball.getVelocity().add(new Vector(0, -0.1, 0)));
                //if(i != tick)
                if((new Random().nextInt(5)) == 0)
                    PaintMgr.PaintHightestBlock(inkball.getLocation(), p, false, true);
                if(inkball.isDead())
                    cancel();
                
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
