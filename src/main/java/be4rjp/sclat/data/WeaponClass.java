package be4rjp.sclat.data;

/**
 *
 * @author Be4rJP
 */
public class WeaponClass {
    
    private String WeaponClassName;
    private MainWeapon mainweapon;
    
    public WeaponClass(String weaponclassname){this.WeaponClassName = weaponclassname;}
    
    public MainWeapon getMainWeapon(){return this.mainweapon;}
    
    public void setMainWeapon(MainWeapon mw){this.mainweapon = mw;}
}
