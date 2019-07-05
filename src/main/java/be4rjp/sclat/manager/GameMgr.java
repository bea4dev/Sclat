package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.Shooter;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;




/**
 *
 * @author Be4rJP
 */
public class GameMgr implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        if(DataMgr.getPlayerDataMap().containsKey(player)){
            if(DataMgr.getPlayerData(player).isInMatch())
                return;
        }
        player.setGameMode(GameMode.ADVENTURE);
        PlayerData data = new PlayerData(player);
        data.setWeaponClass(DataMgr.getWeaponClass("わかばシューター"));
        DataMgr.setPlayerData(player, data);
        //MatchMgr.PlayerJoinMatch(player);
        player.setWalkSpeed(0.2F);
        SquidMgr.SquidRunnable(player);
        String WorldName = conf.getConfig().getString("Lobby.WorldName");
        World w = getServer().getWorld(WorldName);
            
        int ix = conf.getConfig().getInt("Lobby.X");
        int iy = conf.getConfig().getInt("Lobby.Y");
        int iz = conf.getConfig().getInt("Lobby.Z");
        int iyaw = conf.getConfig().getInt("Lobby.Yaw");
        Location il = new Location(w, ix, iy, iz);
        il.setYaw(iyaw);
        
        player.teleport(il);
        ItemStack join = new ItemStack(Material.CHEST);
        ItemMeta joinmeta = join.getItemMeta();
        joinmeta.setDisplayName("メインメニュー");
        join.setItemMeta(joinmeta);
        player.getInventory().setItem(0, join);
        //Shooter.ShooterRunnable(player);
        
        //SquidMgr.SquidRunnable(player);
    }
    
    @EventHandler
    public void onDamageByFall(EntityDamageEvent event){
        if (event.getEntity() instanceof Player){
            if(event.getCause() == DamageCause.FALL)
                event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlaceBlockByEntity(EntityChangeBlockEvent event){
        if (!(event.getEntity() instanceof Player)){
            event.setCancelled(true);
        }
    
    }
    
}
