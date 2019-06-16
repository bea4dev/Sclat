package be4rjp.sclat.data;

import org.bukkit.Location;
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
    private Location matchloc;
    private int playernumber = 0;
    private boolean canshoot;
    private int tick = 0;
    private boolean issquid = false;
    private boolean isonink = false;
    
    public PlayerData(Player player){this.player = player;}
   
    public Match getMatch(){return match;}
   
    public Team getTeam(){return team;}
   
    public boolean isInMatch(){return inmatch;}
    
    public boolean getCanShoot(){return canshoot;}
    
    public WeaponClass getWeaponClass(){return weaponclass;}
    
    //public MainWeapon getMainWeapon(){return mainweapon;}
    
    public Location getMatchLocation(){return this.matchloc;}
    
    public int getPlayerNumber(){return this.playernumber;}
    
    public int getTick(){return this.tick;}
    
    public boolean getIsSquid(){return this.issquid;}
    
    public boolean getIsOnInk(){return this.isonink;}
    
    
    public void setMatch(Match match){this.match = match;}
    
    public void setTeam(Team team){this.team = team;}
    
    public void setIsInMatch(boolean isinmatch){this.inmatch = isinmatch;}
    
    public void setWeaponClass(WeaponClass weaponclass){this.weaponclass = weaponclass;}
    
    //public void setMainWeapon(MainWeapon mainweapon){this.mainweapon = mainweapon;}
    
    public void setMatchLocation(Location l){this.matchloc = l;}
    
    public void setPlayerNumber(int i){this.playernumber = i;}
    
    public void setTick(int i){this.tick = i;}
    
    public void setCanShoot(boolean canshoot){this.canshoot = canshoot;}
    
    public void setIsSquid(boolean is){this.issquid = is;}
    
    public void setIsOnInk(boolean is){this.isonink = is;}
    
    public void reset(){
        this.inmatch = false;
        this.playernumber = 0;
        this.tick = 0;
        this.inmatch = false;
        this.isonink = false;
    }
    
}
