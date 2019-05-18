package be4rjp.sclat;

import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.ColorMgr;
import be4rjp.sclat.manager.GameMgr;
import be4rjp.sclat.manager.MatchMgr;
import org.bukkit.plugin.PluginManager;
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
        //conf.LoadConfig();
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new GameMgr(), this);
        ColorMgr.SetupColor();
        MatchMgr.MatchSetup();
        
    }


    @Override
    public void onDisable() {
        conf.SaveConfig();
    }
    
}
