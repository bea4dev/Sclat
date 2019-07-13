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
    private int paintrandom;
    private double maxpaintdis;
    private float needink;
    private double damage;
    private int maxcharge;
    private int rollershootquantity;
    
    
    
    public MainWeapon(String weaponname){this.WeaponName = weaponname;}
    
    public String getWeaponType(){return this.WeaponType;}
    
    public ItemStack getWeaponIteamStack(){return this.weaponitem;}
    
    public double getRandom(){return random;}
    
    public int getDistanceTick(){return distancetick;}
    
    public double getShootSpeed(){return shootspeed;}
    
    public int getShootTick(){return shoottick;}
    
    public int getPaintRandom(){return this.paintrandom;}
    
    public double getMaxPaintDis(){return this.maxpaintdis;}
    
    public float getNeedInk(){return this.needink;}
    
    public double getDamage(){return this.damage;}
    
    public int getMaxCharge(){return this.maxcharge;}
    
    public int getRollerShootQuantity(){return this.rollershootquantity;}
    
    
    public void setWeaponType(String WT){this.WeaponType = WT;}
    
    public void setWeaponItemStack(ItemStack is){this.weaponitem = is;}
    
    public void setRandom(double random){this.random = random;}
    
    public void setDistanceTick(int distick){this.distancetick = distick;}
    
    public void setShootSpeed(double speed){this.shootspeed = speed;}
    
    public void setShootTick(int shoottick){this.shoottick = shoottick;}
    
    public void setPaintRandom(int r){this.paintrandom = r;}
    
    public void setMaxPaintDis(double max){this.maxpaintdis = max;}
    
    public void setNeedInk(float ink){this.needink = ink;}
    
    public void setDamage(double damage){this.damage = damage;}
    
    public void setMaxCharge(int max){this.maxcharge = max;}
    
    public void setRollerShootQuantity(int i){this.rollershootquantity = i;}
    
}
