package be4rjp.sclat;

import java.io.File;
import static org.bukkit.Bukkit.getLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Be4rJP
 */
public class Config {
    private FileConfiguration ps;
    private FileConfiguration conf;
    private File psf = new File("plugins/Sclat", "Weapons.yml");
    private File conff = new File("plugins/Sclat", "config.yml");
    public synchronized void LoadConfig(){
        ps = YamlConfiguration.loadConfiguration(psf);
        conf = YamlConfiguration.loadConfiguration(conff);
    }
    
    public synchronized void SaveConfig(){
        try{
        ps.save(psf);
        }catch(Exception e){
            getLogger().warning("Failed to save config files!");
        }
    }
    
    public FileConfiguration getConfig(){
        return conf;
    }
    
    public FileConfiguration getWeapons(){
        return ps;
    }
}
