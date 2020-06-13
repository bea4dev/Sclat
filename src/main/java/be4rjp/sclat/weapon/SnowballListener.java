
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_13_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R1.PlayerConnection;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class SnowballListener implements Listener {
    @EventHandler
    public void onBlockHit(ProjectileHitEvent event){
        if(DataMgr.getSnowballIsHitMap().containsKey((Snowball)event.getEntity())){
            if(event.getEntity().getCustomName() == null){
                DataMgr.setSnowballIsHit((Snowball)event.getEntity(), true);
            }else{
                if(event.getHitBlock() != null)
                    DataMgr.setSnowballIsHit((Snowball)event.getEntity(), true);
                if(event.getHitEntity() != null){
                    Snowball ball = (Snowball)event.getEntity();
                    Vector vec = ball.getVelocity();
                    Location loc = ball.getLocation();
                    Snowball ball2 = (Snowball)ball.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX() + vec.getX(), loc.getY() + vec.getY(), loc.getZ() + vec.getZ()), EntityType.SNOWBALL);
                    ball2.setVelocity(vec);
                    ball2.setCustomName(ball.getCustomName());
                    DataMgr.getSnowballNameMap().put(ball.getCustomName(), ball2);
                    DataMgr.setSnowballIsHit(ball2, false);
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        PlayerConnection connection = ((CraftPlayer) o_player).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutEntityDestroy(ball2.getEntityId()));
                    }
                } 
            }
            return;
        }

        if(event.getEntity() instanceof Snowball){
            if(event.getHitBlock() != null){
                Player shooter = (Player)event.getEntity().getShooter();
                PaintMgr.Paint(event.getHitBlock().getLocation(), shooter, true);
                shooter.getWorld().playSound(event.getHitBlock().getLocation(), Sound.ENTITY_SLIME_ATTACK, 0.3F, 2.0F);
            }
            if(event.getHitEntity() != null){
                if(event.getHitEntity() instanceof Player){
                    //AntiNoDamageTime
                    Player target = (Player)event.getHitEntity();
                    BukkitRunnable task = new BukkitRunnable(){
                        Player p = target;
                        @Override
                        public void run(){
                            target.setNoDamageTicks(0);
                        }
                    };
                    task.runTaskLater(Main.getPlugin(), 1);

                    Timer timer = new Timer(false);
                    TimerTask t = new TimerTask(){
                        Player p = target;
                        @Override
                        public void run(){
                            try{
                                target.setNoDamageTicks(0);
                                timer.cancel();
                            }catch(Exception e){
                                timer.cancel();
                            }
                        }
                    };
                    timer.schedule(t, 25);
                }
            }
        }
        
    }
    
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
        
        if(!(event.getDamager() instanceof Projectile))
            return;

        if(DataMgr.getSnowballIsHitMap().containsKey((Snowball)event.getDamager())){
            if(event.getEntity().getCustomName() == null)
                DataMgr.setSnowballIsHit((Snowball)event.getDamager(), true);
        }else{
            Projectile projectile = (Projectile)event.getDamager();
            Player shooter = (Player)projectile.getShooter();
            if(event.getEntity() instanceof Player){
                Player target = (Player)event.getEntity();
                if(DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                    if(!DataMgr.getPlayerData(shooter).getIsUsingSP())
                        SPWeaponMgr.addSPCharge(shooter);
                    if(DataMgr.getPlayerData(target).getArmor() > 40){
                        Vector vec = projectile.getVelocity();
                        Vector v = new Vector(vec.getX(), 0, vec.getZ()).normalize();
                        target.setVelocity(new Vector(v.getX(), 0.2, v.getZ()).multiply(0.3));
                    }
                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage() * Gear.getGearInfluence(shooter, Gear.Type.MAIN_SPEC_UP)){
                        DamageMgr.SclatGiveDamage(target, DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage() * Gear.getGearInfluence(shooter, Gear.Type.MAIN_SPEC_UP));
                        PaintMgr.Paint(target.getLocation(), shooter, true);
                    }else{
                        target.setGameMode(GameMode.SPECTATOR);
                        if(projectile.getCustomName() == null)
                            DeathMgr.PlayerDeathRunnable(target, shooter, "killed");
                        else{
                            if(projectile.getCustomName().equals("Sprinkler"))
                                DeathMgr.PlayerDeathRunnable(target, shooter, "subWeapon");
                            if(projectile.getCustomName().equals("Amehurasi"))
                                DeathMgr.PlayerDeathRunnable(target, shooter, "spWeapon");
                        }
                        PaintMgr.Paint(target.getLocation(), shooter, true);
                    }
                    //AntiDamageTime
                    BukkitRunnable task = new BukkitRunnable(){
                        Player p = target;
                        @Override
                        public void run(){
                            target.setNoDamageTicks(0);
                        }
                    };
                    task.runTaskLater(Main.getPlugin(), 1);
                    
                    Timer timer = new Timer(false);
                    TimerTask t = new TimerTask(){
                        Player p = target;
                        @Override
                        public void run(){
                            try{
                                target.setNoDamageTicks(0);
                                timer.cancel();
                            }catch(Exception e){
                                timer.cancel();
                            }
                        }
                    };
                    timer.schedule(t, 25);
                    
                }
            }else if(event.getEntity() instanceof ArmorStand){
                ArmorStand as = (ArmorStand) event.getEntity();
                ArmorStandMgr.giveDamageArmorStand(as, DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage(), shooter);
            }
        }
        
    }
}
