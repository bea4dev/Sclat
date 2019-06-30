
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.WeaponClass;

/**
 *
 * @author Be4rJP
 */
public class WeaponClassMgr {
    public synchronized static void WeaponClassSetup(){
        for (String classname : conf.getClassConfig().getConfigurationSection("WeaponClass").getKeys(false)){
            String WeaponName = conf.getClassConfig().getString("WeaponClass." + classname + ".MainWeaponName");
            String SubWeaponName = conf.getClassConfig().getString("WeaponClass." + classname + ".SubWeaponName");
            Main.getPlugin().getLogger().info(SubWeaponName);
            Main.getPlugin().getLogger().info(WeaponName);
            //String SPWeaponName = conf.getMapConfig().getString("WeaponClass." + classname + ".SPWeaponName");
            WeaponClass wc = new WeaponClass(classname);
                wc.setMainWeapon(DataMgr.getWeapon(WeaponName));
                wc.setSubWeaponName(SubWeaponName);
            DataMgr.setWeaponClass(classname, wc);
        }
    }
    
}
