
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SubWeaponMgr;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
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
            if(equalWeapon(player)){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setTick(0);
                if(!data.getWeaponClass().getMainWeapon().getWeaponType().equals("Shooter") && !data.getWeaponClass().getMainWeapon().getWeaponType().equals("Blaster"))
                    data.setIsHolding(true);
                if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Blaster"))
                    Blaster.ShootBlaster(player);
                if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Roller") && data.getCanShoot()){
                    data.setCanShoot(false);
                    Roller.ShootPaintRunnable(player);
                }  
            }
        }
        if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))
            if(DataMgr.getPlayerData(player).isInMatch())
                SubWeaponMgr.UseSubWeapon(player, DataMgr.getPlayerData(player).getWeaponClass().getSubWeaponName());
        
    }
    
    @EventHandler
    public void onBlockHit(ProjectileHitEvent event){
        Player shooter = (Player)event.getEntity().getShooter();
        PaintMgr.Paint(event.getHitBlock().getLocation(), shooter, true);
        shooter.getWorld().playSound(event.getHitBlock().getLocation(), Sound.ENTITY_SLIME_ATTACK, 0.3F, 2.0F);
    }
    
    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        event.setCancelled(true);
        
        
        
        
        Projectile projectile = (Projectile)event.getDamager();
        Player shooter = (Player)projectile.getShooter();
        if(event.getEntity() instanceof Player){
            Player target = (Player)event.getEntity();
            if(DataMgr.getPlayerData(shooter).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage()){
                    DamageMgr.SclatGiveDamage(target, DataMgr.getPlayerData(shooter).getWeaponClass().getMainWeapon().getDamage());
                    PaintMgr.Paint(target.getLocation(), shooter, true);
                }else{
                    target.setGameMode(GameMode.SPECTATOR);
                    DeathMgr.PlayerDeathRunnable(target, shooter, "killed");
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
        }
        
    }
    
    @EventHandler
    public void onEntityDamage (EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getCause() == DamageCause.VOID) {
                e.setCancelled(true);
                if(DataMgr.getPlayerData(player).isInMatch())
                    DeathMgr.PlayerDeathRunnable(player, player, "fall");
                else
                    player.teleport(Main.lobby);
                
            }
        }
    }
    
    public boolean equalWeapon(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        String wname = data.getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName();
        String itemname = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        if(wname.equals(itemname.substring(0, wname.length())))
            return true;
        return false;
    }
   
}
