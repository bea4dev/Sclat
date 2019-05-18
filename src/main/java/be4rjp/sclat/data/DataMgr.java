
package be4rjp.sclat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class DataMgr {
    private static Map<Player, PlayerData> playerdata = new HashMap<>();
    private static Map<Integer, Match> matchdata = new HashMap<>();
    private static Map<Integer, Team> teamdata = new HashMap<>();
    private static Map<String, Color> colordata = new HashMap<>();  
    private static Map<String, WeaponClass> weaponclassdata = new HashMap<>();  
    private static Map<String, MainWeapon> weapondata = new HashMap<>();  
    private static List<Color> list;
    
    public static PlayerData getPlayerData(Player player){return playerdata.get(player);}
    public static Match getMatchFromId(int id){return matchdata.get(id);}
    public static Team getTeamFromId(int id){return teamdata.get(id);}
    public static Color getColor(String name){return colordata.get(name);}
    public static WeaponClass getWeaponClass(String weaponclass){return weaponclassdata.get(weaponclass);}
    public static MainWeapon getWeapon(String name){return weapondata.get(name);}
    
    
    public static void setPlayerData(Player player, PlayerData data){playerdata.put(player, data);}
    public static void setMatch(int id, Match match){matchdata.put(id, match);}
    public static void setColor(String name, Color color){colordata.put(name, color);}
    public static void setTeam(int id, Team team){teamdata.put(id, team);}
    public static void setWeaponClass(String WCname, WeaponClass weaponclass){weaponclassdata.put(WCname, weaponclass);}
    public static void setMainWeapon(String MWname, MainWeapon mw){weapondata.put(MWname, mw);}
    
    public static Color getColorRandom(int number){
        list = new ArrayList<>(colordata.values());
        Collections.shuffle(list);
        Color color = list.get(number);
        color.setIsUsed(true);
        return color;   //RandomColor
    }
    
}
