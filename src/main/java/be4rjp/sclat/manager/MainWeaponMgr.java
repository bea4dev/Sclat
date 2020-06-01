
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
            double random = conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".ShootRandom");
            int distick = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".ShootDistance");
            double shootspeed = conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".ShootSpeed");
            int shoottick = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".ShootTick");
            int paintrandom = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".PaintRandom");
            double maxpaintdis = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".MaxPaintDistance");
            float needink = (float)conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".NeedInk");
            double damage = conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".Damage");
            int maxcharge = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".MaxCharge");
            int rollershootQuantity = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".RollerShootQuantity");
            float usinwalkspeed = (float)conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".UsingWalkSpeed");
            int rollerWidth = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".RollerWidth");
            boolean tatehuri = conf.getWeaponConfig().getBoolean("MainWeapon." + weaponname + ".RollerTatehuri");
            double rollerdamage = conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".RollerDamage");
            float rollerneedink = (float)conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".RollerNeedInk");
            double exh = 0;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".BlasterExHankei"))
                exh = (double)conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".BlasterExHankei");
            int delay = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".Delay");
            int cooltime = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".Cooltime");
            double exd = conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".BlasterExDamage");
            boolean hude = conf.getWeaponConfig().getBoolean("MainWeapon." + weaponname + ".IsBrush");
            double huder = conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".BrushRandom");
            
            
            
            MainWeapon mw = new MainWeapon(weaponname);
            mw.setWeaponType(WeaponType);
            ItemStack is = new ItemStack(WeaponMaterial);
            ItemMeta itemMeta = is.getItemMeta();
            itemMeta.setDisplayName(weaponname);
            is.setItemMeta(itemMeta);
            mw.setWeaponItemStack(is);
            mw.setRandom(random);
            mw.setDistanceTick(distick);
            mw.setShootSpeed(shootspeed);
            mw.setShootTick(shoottick);
            mw.setPaintRandom(paintrandom);
            mw.setMaxPaintDis(maxpaintdis);
            mw.setNeedInk(needink);
            mw.setDamage(damage);
            mw.setMaxCharge(maxcharge);
            mw.setRollerShootQuantity(rollershootQuantity);
            mw.setUsingWalkSpeed(usinwalkspeed);
            mw.setRollerWidth(rollerWidth);
            mw.setRollerDamage(rollerdamage);
            mw.setRollerNeedInk(rollerneedink);
            mw.setCanTatehuri(tatehuri);
            mw.setBlasterExHankei(exh);
            mw.setDelay(delay);
            mw.setCoolTime(cooltime);
            mw.setBlasterExDamage(exd);
            mw.setIsHude(hude);
            mw.setHudeRandom(huder);
            
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".Scope"))
                mw.setScope(conf.getWeaponConfig().getBoolean("MainWeapon." + weaponname + ".Scope"));
            
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".Money"))
                mw.setMoney(conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".Money"));
            else
                mw.setMoney(0);
                
            
            DataMgr.setMainWeapon(weaponname, mw);
        }
    }
}
