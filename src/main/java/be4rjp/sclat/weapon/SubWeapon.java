
package be4rjp.sclat.weapon;


import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.MainWeaponMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.SubWeaponMgr;
import be4rjp.sclat.raytrace.RayTrace;
import be4rjp.sclat.weapon.subweapon.QuickBomb;
import be4rjp.sclat.weapon.subweapon.SplashBomb;
import java.util.ArrayList;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;


/**
 *
 * @author Be4rJP
 */
public class SubWeapon implements Listener{
    //サブウエポンのリスナー部分
    @EventHandler
    public void onClickSubWeapon(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        PlayerData data = DataMgr.getPlayerData(player);
        
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            SubWeaponMgr.UseSubWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
        }
    }
    
    @EventHandler
    public void onPlayerClick(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        
        RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
        ArrayList<Vector> positions = rayTrace.traverse(4, 0.5);
        check : for(int i = 0; i < positions.size();i++){
            Location position = positions.get(i).toLocation(player.getLocation().getWorld());
            if(position.getBlock().getType().toString().contains("SIGN")){
                return;
            }
        }
        
        if(event.getAnimationType() == PlayerAnimationType.ARM_SWING){
            if(DataMgr.getPlayerData(player).isInMatch())
                SubWeaponMgr.UseSubWeapon(player, DataMgr.getPlayerData(player).getWeaponClass().getSubWeaponName());
        }
    }
    
    @EventHandler
    public void PlayerRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        
        if(!DataMgr.getPlayerData(player).isInMatch()) return;
        
        SubWeaponMgr.UseSubWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
    }
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event){
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getItemMeta() == null || player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
            return;
        if(!DataMgr.getPlayerData(player).isInMatch()) return;
    
        SubWeaponMgr.UseSubWeapon(player, player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
    }
}
