package be4rjp.sclat;

import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.ColorMgr;
import be4rjp.sclat.manager.GameMgr;
import be4rjp.sclat.manager.MapDataMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.NPCMgr;
import java.util.logging.Level;

import org.bukkit.WorldCreator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Be4rJP
 */
public class Main extends JavaPlugin {
    
    public static Config conf = new Config();
    
    private static Main plugin;
    
    public NPCMgr npcmgr;
    

    @Override
    public void onEnable() {
        plugin = this;	
        this.npcmgr = new NPCMgr();
        getLogger().info("Loading config files...");
        conf.LoadConfig();
        for (String mapname : conf.getMapConfig().getConfigurationSection("Maps").getKeys(false))
            getServer().createWorld(new WorldCreator(conf.getMapConfig().getString("Maps." + mapname + ".WorldName")));
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new GameMgr(), this);
        ColorMgr.SetupColor();
        MapDataMgr.SetupMap();
        MatchMgr.MatchSetup();
    }


    @Override
    public void onDisable() {
        conf.SaveConfig();
    }
    
    public static Main getPlugin(){
        return plugin;
    }
    
}


