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
    
    public MainWeapon(String weaponname){this.WeaponName = weaponname;}
    
    public String getWeaponType(){return this.WeaponType;}
    
    public ItemStack getWeaponIteamStack(){return this.weaponitem;}
    
    public void setWeaponType(String WT){this.WeaponType = WT;}
    
    public void setWeaponItemStack(ItemStack is){this.weaponitem = is;}
}
