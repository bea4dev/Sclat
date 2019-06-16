
package be4rjp.sclat.weapon;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SubWeapon {
    //サブウエポンのリスナー部分
    @EventHandler
    public void onClickSubWeapon(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        PlayerData data = DataMgr.getPlayerData(player);
        if(action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)){
            if("スプラッシュボム".equals(data.getWeaponClass().getSubWeaponName())){
                BukkitRunnable task = new BukkitRunnable(){
                    LivingEntity zonbi;
                    Player p = player;
                    int i = 0;
                    @Override
                    public void run(){
                        Entity e = p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
                        i++;
                    }
                };
            }
        }
    }
}
