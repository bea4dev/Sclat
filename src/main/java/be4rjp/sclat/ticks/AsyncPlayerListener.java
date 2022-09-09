package be4rjp.sclat.ticks;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AsyncPlayerListener implements Listener {
    
    @EventHandler
    public void online(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AsyncThreadManager.onlinePlayers.add(player);
    }
    
    @EventHandler
    public void offline(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AsyncThreadManager.onlinePlayers.remove(player);
    }
    
}
