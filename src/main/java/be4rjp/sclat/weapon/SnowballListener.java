
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import java.util.Random;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SnowballListener implements Listener {
    @EventHandler
    public void onBlockHit(ProjectileHitEvent event){
        
        
        if(DataMgr.getSnowballIsHitMap().containsKey((Projectile)event.getEntity())){
            DataMgr.setSnowballIsHit((Projectile)event.getEntity(), true);
        }else{
            Player shooter = (Player)event.getEntity().getShooter();
            PaintMgr.Paint(event.getHitBlock().getLocation(), shooter, true);
            shooter.getWorld().playSound(event.getHitBlock().getLocation(), Sound.ENTITY_SLIME_ATTACK, 0.3F, 2.0F);
        }
    }
    
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
        
        if(!(event.getDamager() instanceof Projectile))
            return;

        if(DataMgr.getSnowballIsHitMap().containsKey((Snowball)event.getDamager())){
            DataMgr.setSnowballIsHit((Snowball)event.getDamager(), true);
        }else{
            Projectile projectile = (Projectile)event.getDamager();
            Player shooter = (Player)projectile.getShooter();
            if(event.getEntity() instanceof Player){
                Player target = (Player)event.getEntity();
                if(DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                    if(!DataMgr.getPlayerData(shooter).getIsUsingSP())
                        SPWeaponMgr.addSPCharge(shooter);
                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage()){
                        DamageMgr.SclatGiveDamage(target, DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage());
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
                }
            }else if(event.getEntity() instanceof ArmorStand){
                ArmorStand as = (ArmorStand) event.getEntity();
                ArmorStandMgr.giveDamageArmorStand(as, DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage(), shooter);
            }
        }
        
    }
}
