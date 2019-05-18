package be4rjp.sclat.data;

import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PlayerData {
    private Player player;
    private String classname;
    private Match match;
    private Team team;
    private boolean inmatch = false;
    private WeaponClass weaponclass;
    private MainWeapon mainweapon;
    
    public PlayerData(Player player){this.player = player;}
   
    public Match getMatch(){return match;}
   
    public Team getTeam(){return team;}
   
    public boolean isInMatch(){return inmatch;}
    
    public WeaponClass getWeaponClass(){return weaponclass;}
    
    public MainWeapon getMainWeapon(){return mainweapon;}
    
    
    public void setMatch(Match match){this.match = match;}
    
    public void setTeam(Team team){this.team = team;}
    
    public void setIsInMatch(boolean isinmatch){this.inmatch = isinmatch;}
    
    public void setWeaponClass(WeaponClass weaponclass){this.weaponclass = weaponclass;}
    
    public void setMainWeapon(MainWeapon mainweapon){this.mainweapon = mainweapon;}
    
    
}
