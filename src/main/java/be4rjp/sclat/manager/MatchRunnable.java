
package be4rjp.sclat.manager;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class MatchRunnable extends BukkitRunnable{
    
    private final JavaPlugin plugin;

    private int counter;
    
    private Player player;

    public MatchRunnable(JavaPlugin plugin, int counter, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.counter = counter;
    }

    @Override
    public void run() {
        if (counter > 0) {
            player.sendTitle("", "§a試合開始まで後" + counter-- + "秒", 4, 12, 4);
        }
    }
    
}
