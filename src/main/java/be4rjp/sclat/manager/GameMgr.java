package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;




/**
 *
 * @author Be4rJP
 */
public class GameMgr implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        PlayerData data = new PlayerData(player);
        data.setWeaponClass(DataMgr.getWeaponClass("わかばシューター"));
        DataMgr.setPlayerData(player, data);
        MatchMgr.PlayerJoinMatch(player);
        player.setWalkSpeed(0.2F);
        
        //SquidMgr.SquidRunnable(player);
    }
    
    @EventHandler
    public void noDamageByFall(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            if(event.getCause() == DamageCause.FALL)
                event.setCancelled(true);
        }
    }
    
}
