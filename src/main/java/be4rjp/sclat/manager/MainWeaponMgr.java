
package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MainWeapon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Be4rJP
 */
public class MainWeaponMgr {
    public synchronized static void SetupMainWeapon(){
        for (String weaponname : conf.getWeaponConfig().getConfigurationSection("MainWeapon").getKeys(false)){
            String WeaponType = conf.getWeaponConfig().getString("MainWeapon." + weaponname + ".WeaponType");
            Material WeaponMaterial = Material.getMaterial(conf.getWeaponConfig().getString("MainWeapon." + weaponname + ".WeaponMaterial"));
            MainWeapon mw = new MainWeapon(weaponname);
            mw.setWeaponType(WeaponType);
            ItemStack is = new ItemStack(WeaponMaterial);
            ItemMeta itemMeta = is.getItemMeta();
            itemMeta.setDisplayName(weaponname);
            is.setItemMeta(itemMeta);
            mw.setWeaponItemStack(is);
            DataMgr.setMainWeapon(weaponname, mw);
        }
    }
}
