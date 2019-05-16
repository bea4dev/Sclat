package be4rjp.sclat;

import be4rjp.sclat.data.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Be4rJP
 */
public class Main extends JavaPlugin {
    
    public static Config conf = new Config();
    

    @Override
    public void onEnable() {
        getLogger().info("Loading config files...");
        conf.LoadConfig();
    }


    @Override
    public void onDisable() {
        conf.SaveConfig();
    }
    
}
