package be4rjp.sclat.data;

/**
 *
 * @author Be4rJP
 */
public class MainWeapon {
    private String WeaponName;
    private String WeaponType;
    
    public MainWeapon(String weaponname){this.WeaponName = weaponname;}
    
    public String getWeaponType(){return this.WeaponType;}
    
    public void setWeaponType(String WT){this.WeaponType = WT;}
}
