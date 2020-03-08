package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.PlayerSettings;
import be4rjp.sclat.weapon.Shooter;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.ChatColor;
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
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;




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
        
        String uuid = player.getUniqueId().toString();
        PlayerSettings settings = new PlayerSettings(player);
        
        if(conf.getPlayerSetiings().contains("Settings." + uuid)){
            if(conf.getPlayerSetiings().getString("Settings." + uuid).substring(0,1).equals("0"))
                settings.S_ShowEffect_Shooter();             
            if(conf.getPlayerSetiings().getString("Settings." + uuid).substring(1,2).equals("0"))
                settings.S_ShowEffect_ChargerLine();
            if(conf.getPlayerSetiings().getString("Settings." + uuid).substring(2,3).equals("0"))
                settings.S_ShowEffect_ChargerShot();
            if(conf.getPlayerSetiings().getString("Settings." + uuid).substring(3,4).equals("0"))
                settings.S_ShowEffect_RollerRoll();
            if(conf.getPlayerSetiings().getString("Settings." + uuid).substring(4,5).equals("0"))
                settings.S_ShowEffect_RollerShot();
        }else{
            conf.getPlayerSetiings().set("Settings." + uuid, "111111");
        }
            
        data.setSettings(settings);
        data.setWeaponClass(DataMgr.getWeaponClass("わかばシューター"));
        DataMgr.setPlayerData(player, data);
        //MatchMgr.PlayerJoinMatch(player);
        player.setWalkSpeed(0.2F);
        SquidMgr.SquidRunnable(player);
        
        Main.getPlugin().getLogger().info(Main.lobby.getWorld().getName());
        
        player.teleport(Main.lobby);
        ItemStack join = new ItemStack(Material.CHEST);
        ItemMeta joinmeta = join.getItemMeta();
        joinmeta.setDisplayName("メインメニュー");
        join.setItemMeta(joinmeta);
        player.getInventory().clear();
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
    
    @EventHandler
    public void onPickItem(EntityPickupItemEvent event){
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onbWeatherChange(WeatherChangeEvent event){
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
    
}
