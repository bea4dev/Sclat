package be4rjp.sclat.data;

/**
 *
 * @author Be4rJP
 */
public class WeaponClass {
    
    private String WeaponClassName;
    private MainWeapon mainweapon;
    private String subweaponname;
    
    public WeaponClass(String weaponclassname){this.WeaponClassName = weaponclassname;}
    
    public MainWeapon getMainWeapon(){return this.mainweapon;}
    
    public String getSubWeaponName(){return this.subweaponname;}
    
    public void setMainWeapon(MainWeapon mw){this.mainweapon = mw;}
    
    public void setSubWeaponName(String name){this.subweaponname = name;}
}
