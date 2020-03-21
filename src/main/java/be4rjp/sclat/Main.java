package be4rjp.sclat;

import be4rjp.sclat.GUI.ClickListener;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.listener.SquidListener;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.ColorMgr;
import be4rjp.sclat.manager.GameMgr;
import be4rjp.sclat.manager.MainWeaponMgr;
import be4rjp.sclat.manager.MapDataMgr;
import be4rjp.sclat.manager.MatchMgr;
import be4rjp.sclat.manager.NPCMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.weapon.MainWeapon;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getServer;
import org.bukkit.Location;
import org.bukkit.World;

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
    
    public static Location lobby;
    
    //重複しない数字
    //ボム等で使用
    private static int NDNumber = 0;
    

    @Override
    public void onEnable() {
        plugin = this;	
        
        //APICheck
        boolean NoteBlockAPI = true;
        if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")){
            getLogger().severe("*** NoteBlockAPI is not installed or not enabled. ***");
            NoteBlockAPI = false;
            return;
        }
        
        getLogger().info("Loading config files...");
        conf.LoadConfig();
        for (String mapname : conf.getMapConfig().getConfigurationSection("Maps").getKeys(false))
            Bukkit.createWorld(new WorldCreator(conf.getMapConfig().getString("Maps." + mapname + ".WorldName")));
        String WorldName = conf.getConfig().getString("Lobby.WorldName");
        Bukkit.createWorld(new WorldCreator(WorldName));
        //getServer().getWorlds().add(w);

        World w = Bukkit.getWorld(WorldName);
        getLogger().info(w.getName());
        int ix = conf.getConfig().getInt("Lobby.X");
        int iy = conf.getConfig().getInt("Lobby.Y");
        int iz = conf.getConfig().getInt("Lobby.Z");
        int iyaw = conf.getConfig().getInt("Lobby.Yaw");
        lobby = new Location(w, ix, iy, iz);
        lobby.setYaw(iyaw);
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new GameMgr(), this);
        pm.registerEvents(new SquidListener(), this);
        pm.registerEvents(new ClickListener(), this);
        pm.registerEvents(new be4rjp.sclat.weapon.MainWeapon(), this);
        pm.registerEvents(new be4rjp.sclat.weapon.SubWeapon(), this);
        pm.registerEvents(new be4rjp.sclat.weapon.SPWeapon(), this);
        
        getLogger().info("registerEvents ok");
        ColorMgr.SetupColor();
        getLogger().info("SetupColor() ok");
        MainWeaponMgr.SetupMainWeapon();
        getLogger().info("SetupMainWeapon() ok");
        WeaponClassMgr.WeaponClassSetup();
        getLogger().info("WeaponClassSetup() ok");
        MapDataMgr.SetupMap();
        getLogger().info("SetupMap() ok");
        MatchMgr.MatchSetup();
        getLogger().info("MatchSetup() ok");
        ArmorStandMgr.ArmorStandSetup();
    }


    @Override
    public void onDisable() {
        //塗りリセット
        for(PaintData data : DataMgr.getBlockDataMap().values()){
            
                data.getBlock().setType(data.getOriginalType());
                data = null;
        }
        DataMgr.getBlockDataMap().clear();
        conf.SaveConfig();
        
    }
    
    public static Main getPlugin(){
        return plugin;
    }
    
    public static int getNotDuplicateNumber(){
        NDNumber++;
        return NDNumber;
    }
    
}


