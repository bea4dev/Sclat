package be4rjp.sclat.data;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Be4rJP
 */
public class MainWeapon {
    private String WeaponName;
    private String WeaponType;
    private ItemStack weaponitem;
    private double random;
    private int distancetick;
    private double shootspeed;
    private int shoottick;
    
    public MainWeapon(String weaponname){this.WeaponName = weaponname;}
    
    public String getWeaponType(){return this.WeaponType;}
    
    public ItemStack getWeaponIteamStack(){return this.weaponitem;}
    
    public double getRandom(){return random;}
    
    public int getDistanceTick(){return distancetick;}
    
    public double getShootSpeed(){return shootspeed;}
    
    public int getShootTick(){return shoottick;}
    
    
    public void setWeaponType(String WT){this.WeaponType = WT;}
    
    public void setWeaponItemStack(ItemStack is){this.weaponitem = is;}
    
    public void setRandom(double random){this.random = random;}
    
    public void setDistanceTick(int distick){this.distancetick = distick;}
    
    public void setShootSpeed(double speed){this.shootspeed = speed;}
    
    public void setShootTick(int shoottick){this.shoottick = shoottick;}
}
