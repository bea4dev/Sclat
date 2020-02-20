
package be4rjp.sclat.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.block.Block;
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
    private static Map<String, MapData> mapdata = new HashMap<>(); 
    private static Map<MapData, TeamLoc> locdata = new HashMap<>(); 
    private static Map<Block, PaintData> blockdata = new HashMap<>(); 
    //private static Map<Match, PaintData> paintdata = new HashMap<>(); 
    private static List<Color> list = new ArrayList<>();
    private static List<MapData> maplist = new ArrayList<>();
    
    public static PlayerData getPlayerData(Player player){return playerdata.get(player);}
    public static Match getMatchFromId(int id){return matchdata.get(id);}
    public static Team getTeamFromId(int id){return teamdata.get(id);}
    public static Color getColor(String name){return colordata.get(name);}
    public static WeaponClass getWeaponClass(String weaponclass){return weaponclassdata.get(weaponclass);}
    public static MainWeapon getWeapon(String name){return weapondata.get(name);}
    public static MapData getMap(String name){return mapdata.get(name);}
    public static TeamLoc getTeamLoc(MapData map){return locdata.get(map);}
    public static PaintData getPaintDataFromBlock(Block block){return blockdata.get(block);}
    //public static PaintData getPaintDataFromMatch(Match match){return paintdata.get(match);}
    
    
    public static void setPlayerData(Player player, PlayerData data){playerdata.put(player, data);}
    public static void setMatch(int id, Match match){matchdata.put(id, match);}
    public static void setColor(String name, Color color){colordata.put(name, color);}
    public static void setTeam(int id, Team team){teamdata.put(id, team);}
    public static void setWeaponClass(String WCname, WeaponClass weaponclass){weaponclassdata.put(WCname, weaponclass);}
    public static void setMainWeapon(String MWname, MainWeapon mw){weapondata.put(MWname, mw);}
    public static void setMap(String Mname, MapData map){mapdata.put(Mname, map);}
    public static void setTeamLoc(MapData map, TeamLoc loc){locdata.put(map, loc);}
    public static void setPaintDataFromBlock(Block block, PaintData data){blockdata.put(block, data);}
    
    
    public static void addColorList(Color color){list.add(color);}
    public static void addMapList(MapData map){maplist.add(map);}
    //public static void setPaintDataFromMatch(Match match, PaintData data){paintdata.put(match, data);}
    
    public static Map<Block, PaintData> getBlockDataMap(){return blockdata;}
    public static Map<Player, PlayerData> getPlayerDataMap(){return playerdata;}
    //public static Map<Match, PaintData> getPaintDataMap(){return paintdata;}
    
    public static Color getColorRandom(int number){
        
        Color color = list.get(number);
        color.setIsUsed(true);
        return color;   //RandomColor
    }
    
    public static void ColorShuffle(){
        Collections.shuffle(list);
    }
    
    public static void MapDataShuffle(){
        Collections.shuffle(maplist);
    }
    
    public static MapData getMapRandom(int i){
        MapData map = maplist.get(i);
        return map;   //RandomMap
    }
    
}
