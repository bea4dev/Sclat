
package be4rjp.sclat.manager;

import static be4rjp.sclat.Main.conf;

import be4rjp.dadadachecker.ClickType;
import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MainWeapon;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.weapon.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
            boolean man = false;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".IsManeuver"))
                man = conf.getWeaponConfig().getBoolean("MainWeapon." + weaponname + ".IsManeuver");
            int slST = 1;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".SlidingShootTick"))
                slST = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".SlidingShootTick");
            
            double cr = 1.0;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".ChargeRatio"))
                cr = conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".ChargeRatio");
            
            boolean ck = false;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".CanChargeKeep"))
                ck = conf.getWeaponConfig().getBoolean("MainWeapon." + weaponname + ".CanChargeKeep");
            
            int ckt = 0;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".ChargeKeepingTime"))
                ckt = conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".ChargeKeepingTime");
            
            boolean hc = false;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".HanbunChargeKeep"))
                hc = conf.getWeaponConfig().getBoolean("MainWeapon." + weaponname + ".HanbunChargeKeep");
    
            float sn = 0.2F;
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".SlideNeedInk"))
                sn = (float)(conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".SlideNeedInk"));
            
            
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
            mw.setIsManeuver(man);
            mw.setSlidingShootTick(slST);
            mw.setChargeRatio(cr);
            mw.setCanChargeKeep(ck);
            mw.setChargeKeepingTime(ckt);
            mw.setHanbunCharge(hc);
            mw.setSlideNeedINK(sn);
    
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".SPRate"))
                mw.setSPRate(conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".SPRate"));
    
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".MaxRandom"))
                mw.setMaxRandom(conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".MaxRandom"));
    
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".MaxRandomCount"))
                mw.setMaxRandomCount(conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".MaxRandomCount"));
            
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".Scope"))
                mw.setScope(conf.getWeaponConfig().getBoolean("MainWeapon." + weaponname + ".Scope"));
            
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".Money"))
                mw.setMoney(conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".Money"));
            else
                mw.setMoney(0);
    
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".Level"))
                mw.setLevel(conf.getWeaponConfig().getInt("MainWeapon." + weaponname + ".Level"));
            else
                mw.setLevel(0);
            
            if(conf.getWeaponConfig().contains("MainWeapon." + weaponname + ".InHoldWalkSpeed"))
                mw.setInHoldSpeed((float)conf.getWeaponConfig().getDouble("MainWeapon." + weaponname + ".InHoldWalkSpeed"));
            else
                mw.setInHoldSpeed((float)conf.getConfig().getDouble("PlayerWalkSpeed"));
            
            DataMgr.setMainWeapon(weaponname, mw);
        }
    }
    
    public static boolean equalWeapon(Player player){
        try {
            PlayerData data = DataMgr.getPlayerData(player);
            String wname = data.getWeaponClass().getMainWeapon().getWeaponIteamStack().getItemMeta().getDisplayName();
            if (player.getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null)
                return false;
            String itemname = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            if (itemname.length() >= wname.length())
                if (wname.equals(itemname.substring(0, wname.length())))
                    return true;
            return false;
        }catch (Exception e){return false;}
    }
    
    public static void UseMainWeapon(Player player){
        if(equalWeapon(player)){
    
            Main.dadadaCheckerAPI.fireClickEvent(player);
    
            ClickType clickType = Main.dadadaCheckerAPI.getPlayerClickType(player);
            
            PlayerData data = DataMgr.getPlayerData(player);
            if(data.getCanCharge())
                data.setTick(0);
            if(!data.getWeaponClass().getMainWeapon().getWeaponType().equals("Shooter") && !data.getWeaponClass().getMainWeapon().getWeaponType().equals("Blaster"))
                data.setIsHolding(true);
            if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Blaster"))
                Blaster.ShootBlaster(player);
            if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Burst"))
                Burst.BurstCooltime(player);
            if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Roller")){
                if(data.getWeaponClass().getMainWeapon().getIsHude()){
                    if(data.getCanShoot() || clickType == ClickType.RENDA){
                        data.setCanShoot(false);
                        Brush.ShootPaintRunnable(player);
                    }
                }else {
                    if(data.getCanShoot()) {
                        data.setCanShoot(false);
                        Roller.ShootPaintRunnable(player);
                    }
                }
            }
            if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Bucket"))
                Bucket.ShootBucket(player);
            if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Slosher"))
                Slosher.ShootSlosher(player);
            if(data.getWeaponClass().getMainWeapon().getWeaponType().equals("Kasa") || data.getWeaponClass().getMainWeapon().getWeaponType().equals("Camping"))
                Kasa.ShootKasa(player);
        }
    }
}
