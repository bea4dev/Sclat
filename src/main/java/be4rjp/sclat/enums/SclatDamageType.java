package be4rjp.sclat.enums;

public enum SclatDamageType {
    
    MAIN_WEAPON("killed"),
    SUB_WEAPON("subWeapon"),
    SP_WEAPON("spWeapon"),
    WATER("water"),
    FALL("fall");
    
    private final String name;
    
    SclatDamageType(String name){
        this.name = name;
    }
    
    public String getName(){
        return this.name;
    }
}
