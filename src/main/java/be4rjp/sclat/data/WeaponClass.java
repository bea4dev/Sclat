package be4rjp.sclat.data;

/**
 *
 * @author Be4rJP
 */
public class WeaponClass {
    
    private final String WeaponClassName;
    private MainWeapon mainweapon;
    private String subweaponname;
    private String spweaponname;
    
    public WeaponClass(String weaponclassname){this.WeaponClassName = weaponclassname;}
    
    public MainWeapon getMainWeapon(){return this.mainweapon;}
    
    public String getSubWeaponName(){return this.subweaponname;}
    
    public String getSPWeaponName(){return this.spweaponname;}
    
    public void setMainWeapon(MainWeapon mw){this.mainweapon = mw;}
    
    public void setSubWeaponName(String name){this.subweaponname = name;}
    
    public void setSPWeaponName(String name){this.spweaponname = name;}
}
