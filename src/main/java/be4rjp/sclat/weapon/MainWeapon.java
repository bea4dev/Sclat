package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.MainWeaponMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.SubWeaponMgr;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
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
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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
        
        if(e.getAction() == null || e.getItem() == null)
            return;
        
        Action action = e.getAction();
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            MainWeaponMgr.UseMainWeapon(player);
        }
        if(action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))
            if(DataMgr.getPlayerData(player).isInMatch())
                SubWeaponMgr.UseSubWeapon(player, DataMgr.getPlayerData(player).getWeaponClass().getSubWeaponName());
        
    }
    
    @EventHandler
    public void onSneek(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        PlayerData data = DataMgr.getPlayerData(player);
        data.setIsSneaking(true);
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                data.setIsSneaking(false);
            }
        };
        task.runTaskLater(Main.getPlugin(), 5);
    }
    
    
    @EventHandler
    public void PlayerRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        MainWeaponMgr.UseMainWeapon(player);
        
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(DataMgr.getPlayerData(player).isInMatch())
                    SPWeaponMgr.UseSPWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
    }
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        Player player = event.getPlayer();
        MainWeaponMgr.UseMainWeapon(player);
        
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(DataMgr.getPlayerData(player).isInMatch())
                    SPWeaponMgr.UseSPWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
    }
    
    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent event){
        Player player = event.getPlayer();
        MainWeaponMgr.UseMainWeapon(player);
        
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(DataMgr.getPlayerData(player).isInMatch())
                    SPWeaponMgr.UseSPWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
    }
    
    @EventHandler
    public void onEntityDamage (EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (e.getCause() == DamageCause.VOID) {
                e.setCancelled(true);
                if(conf.getConfig().getString("WorkMode").equals("Trial")){
                    player.teleport(Main.lobby);
                    return;
                }
                if(DataMgr.getPlayerData(player).isInMatch())
                    DeathMgr.PlayerDeathRunnable(player, player, "fall");
                else
                    player.teleport(Main.lobby);
                
            }
        }
    }
}
