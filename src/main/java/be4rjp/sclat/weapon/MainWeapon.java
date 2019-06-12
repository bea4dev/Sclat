
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PaintMgr;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class MainWeapon implements Listener{
    @EventHandler
    public void onClickWeapon(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Action action = e.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            if(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName())){
                //e.setCancelled(true);
                //DataMgr.getPlayerData(player).setCanShoot(true);
                //Shooter.Shoot(player);
                DataMgr.getPlayerData(player).setTick(0);
                
                BukkitRunnable delay = new BukkitRunnable(){
                    Player p = player;
                    int i = 0;
                    @Override
                    public void run(){
                        //p.getInventory().setItem(0, DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getWeaponIteamStack());
                        //DataMgr.getPlayerData(p).setCanShoot(false);
                        i = i + DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick();
                        //DataMgr.getPlayerData(p).setTick(DataMgr.getPlayerData(p).getTick() + DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
                        
                        if(i < 5){
                            Shooter.Shoot(p);
                            //i = i + DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick();
                            DataMgr.getPlayerData(p).setTick(DataMgr.getPlayerData(p).getTick() + DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
                            //i = i + DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick();
                        }else{
                            DataMgr.getPlayerData(p).setTick(0);
                            cancel();
                        }     
                    }
                };
                //if(DataMgr.getPlayerData(player).getTick() < 5)
                    //delay.runTaskTimer(Main.getPlugin(), 0, DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootTick());
                
            }
        }
        if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)){
            MatchMgr.RollBack(DataMgr.getPlayerData(player).getMatch());
        }
    }
    
    @EventHandler
    public void onBlockHit(ProjectileHitEvent event){
        Player shooter = (Player)event.getEntity().getShooter();
        PaintMgr.Paint(event.getHitBlock().getLocation(), shooter);
    }
    
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        Projectile projectile = (Projectile)event.getDamager();
        Player shooter = (Player)projectile.getShooter();
        if(event.getDamager() instanceof Player){
            Player target = (Player)event.getDamager();
            if(DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam()){
                if(target.getHealth() > DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage()){
                    target.setHealth(target.getHealth() - DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage());
                }else{
                    DeathMgr.PlayerDeathRunnable(target, shooter, "killed");
                }
            }
        }
        
    }
    
    @EventHandler
    public void onEntityDamage (EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getCause() == DamageCause.VOID) {
                e.setCancelled(true);
                DeathMgr.PlayerDeathRunnable(player, player, "fall");
            }
        }
    }
   
}
