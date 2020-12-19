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
    private double maxRandom = 0;
    private int distancetick;
    private double shootspeed;
    private int shoottick;
    private int paintrandom;
    private double maxpaintdis;
    private float needink;
    private double damage;
    private int maxcharge;
    private int rollershootquantity;
    private float UsingWalkSpeed;
    private int rollerWidth;
    private boolean Tatehuri;
    private double rollerdamage;
    private float rollerneedink;
    private boolean scope;
    private double exh;
    private int delay;
    private int cooltime;
    private double exd;
    private boolean hude;
    private double huder;
    private int money;
    private boolean isManeuver = false;
    private int slidingshoottick = 0;
    private double chargeratio = 1.0;
    private float InHoldSpeed = 0.2F;
    private boolean canChargeKeep = false;
    private int chargeKeepingTime = 0;
    private boolean hanbunCharge = false;
    private double SPRate = 1.0;
    private int maxRandomCount = 1;
    private int level = 0;
    private float slideNeedINK = 0.2F;
    
    
    
    public MainWeapon(String weaponname){this.WeaponName = weaponname;}
    
    public String getWeaponType(){return this.WeaponType;}
    
    public ItemStack getWeaponIteamStack(){return this.weaponitem;}
    
    public double getRandom(){return random;}
    
    public double getMaxRandom(){return maxRandom;}
    
    public int getDistanceTick(){return distancetick;}
    
    public double getShootSpeed(){return shootspeed;}
    
    public int getShootTick(){return shoottick;}
    
    public int getPaintRandom(){return this.paintrandom;}
    
    public double getMaxPaintDis(){return this.maxpaintdis;}
    
    public float getNeedInk(){return this.needink;}
    
    public double getDamage(){return this.damage;}
    
    public int getMaxCharge(){return this.maxcharge;}
    
    public int getRollerShootQuantity(){return this.rollershootquantity;}
    
    public float getUsingWalkSpeed(){return this.UsingWalkSpeed;}
    
    public int getRollerWidth(){return this.rollerWidth;}
    
    public boolean getCanTatehuri(){return this.Tatehuri;}
    
    public double getRollerDamage(){return this.rollerdamage;}
    
    public float getRollerNeedInk(){return this.rollerneedink;}
    
    public boolean getScope(){return this.scope;}
    
    public double getBlasterExHankei(){return this.exh;}
    
    public int getDelay(){return this.delay;}
    
    public int getCoolTime(){return this.cooltime;}
    
    public double getBlasterExDamage(){return this.exd;}
    
    public boolean getIsHude(){return this.hude;}
    
    public double getHudeRandom(){return this.huder;}
    
    public int getMoney(){return this.money;}
    
    public boolean getIsManeuver(){return this.isManeuver;}
    
    public int getSlidingShootTick(){return this.slidingshoottick;}
    
    public double getChargeRatio(){return this.chargeratio;}
    
    public float getInHoldSpeed(){return this.InHoldSpeed;}
    
    public boolean getCanChargeKeep(){return this.canChargeKeep;}
    
    public int getChargeKeepingTime(){return this.chargeKeepingTime;}
    
    public boolean getHanbunCharge(){return this.hanbunCharge;}
    
    public double getSPRate(){return this.SPRate;}
    
    public int getMaxRandomCount(){return this.maxRandomCount;}
    
    public int getLevel(){return this.level;}
    
    public float getSlideNeedINK(){return this.slideNeedINK;}
    
    
    public void setWeaponType(String WT){this.WeaponType = WT;}
    
    public void setWeaponItemStack(ItemStack is){this.weaponitem = is;}
    
    public void setRandom(double random){this.random = random;}
    
    public void setMaxRandom(double random){this.maxRandom = random;}
    
    public void setDistanceTick(int distick){this.distancetick = distick;}
    
    public void setShootSpeed(double speed){this.shootspeed = speed;}
    
    public void setShootTick(int shoottick){this.shoottick = shoottick;}
    
    public void setPaintRandom(int r){this.paintrandom = r;}
    
    public void setMaxPaintDis(double max){this.maxpaintdis = max;}
    
    public void setNeedInk(float ink){this.needink = ink;}
    
    public void setDamage(double damage){this.damage = damage;}
    
    public void setMaxCharge(int max){this.maxcharge = max;}
    
    public void setRollerShootQuantity(int i){this.rollershootquantity = i;}
    
    public void setUsingWalkSpeed(float f){this.UsingWalkSpeed = f;}
    
    public void setRollerWidth(int w){this.rollerWidth = w;}
    
    public void setCanTatehuri(boolean t){this.Tatehuri = t;}
    
    public void setRollerDamage(double damage){this.rollerdamage = damage;}
    
    public void setRollerNeedInk(float ink){this.rollerneedink = ink;}
    
    public void setScope(boolean is){this.scope = is;}
    
    public void setBlasterExHankei(double d){this.exh = d;}
    
    public void setDelay(int i){this.delay = i;}
    
    public void setCoolTime(int i){this.cooltime = i;}
    
    public void setBlasterExDamage(double d){this.exd = d;}
    
    public void setIsHude(boolean is){this.hude = is;}
    
    public void setHudeRandom(double d){this.huder = d;}
    
    public void setMoney(int i){this.money = i;}
    
    public void setIsManeuver(boolean is){this.isManeuver = is;}
    
    public void setSlidingShootTick(int i){this.slidingshoottick = i;}
    
    public void setChargeRatio(double r){this.chargeratio = r;}
    
    public void setInHoldSpeed(float s){this.InHoldSpeed = s;}
    
    public void setCanChargeKeep(boolean is){this.canChargeKeep = is;}
    
    public void setChargeKeepingTime(int i){this.chargeKeepingTime = i;}
    
    public void setHanbunCharge(boolean is){this.hanbunCharge = is;}
    
    public void setSPRate(double rate){this.SPRate = rate;}
    
    public void setMaxRandomCount(int count){this.maxRandomCount = count;}
    
    public void setLevel(int level){this.level = level;}
    
    public void setSlideNeedINK(float ink){this.slideNeedINK = ink;}
    
}
