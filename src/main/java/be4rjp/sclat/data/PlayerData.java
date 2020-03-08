package be4rjp.sclat.data;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PlayerData {
    private Player player;
    private PlayerSettings settings;
    private String classname;
    private Match match;
    private Team team;
    private boolean inmatch = false;
    private WeaponClass weaponclass;
    private MainWeapon mainweapon;
    private Location matchloc;
    private int playernumber = 0;
    private boolean canshoot = true;
    private int tick = 0;
    private boolean issquid = false;
    private boolean isonink = false;
    private boolean isHolding = false;
    private boolean isjoined = false;
    private boolean canpaint = false;
    private int killcount = 0;
    private int paintcount = 0;
    private double armor = 0;
    private int spgauge = 0;

    
    public PlayerData(Player player){this.player = player;}
    
    public PlayerSettings getSettings(){return settings;}
   
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
    
    public boolean getIsHolding(){return this.isHolding;}
    
    public boolean getIsJoined(){return this.isjoined;}
    
    public boolean getCanPaint(){return this.canpaint;}
    
    public int getKillCount(){return this.killcount;}
    
    public int getPaintCount(){return this.paintcount;}
    
    public double getArmor(){return this.armor;}
    
    public int getSPGauge(){return this.spgauge;}
    
    
    public void setSettings(PlayerSettings settings){this.settings = settings;}
    
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
    
    public void setIsHolding(boolean is){this.isHolding = is;}
    
    public void setIsJoined(boolean is){this.isjoined = is;}
    
    public void setCanPaint(boolean is){this.canpaint = is;}
    
    public void setArmor(double armor){this.armor = armor;}
    
    public void setSPGauge(int spgauge){this.spgauge = spgauge;}
    
    public void addKillCount(){this.killcount++;}
    
    public void addPaintCount(){this.paintcount++;}
    
    public void addSPGauge(){this.spgauge++;}
    
    public void resetSPGauge(){this.spgauge = 0;}
    
    public void reset(){
        this.inmatch = false;
        this.playernumber = 0;
        this.tick = 0;
        this.inmatch = false;
        this.isonink = false;
        this.isHolding = false;
        this.canpaint = false;
        this.canshoot = true;
        this.killcount = 0;
        this.paintcount = 0;
    }
    
}
