
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
    private static Map<Integer, Team> team = new HashMap<>();
    private static Map<String, Color> colordata = new HashMap<>();  
    private static List<Color> list;
    
    public static PlayerData getPlayerData(Player player){return playerdata.get(player);}
    public static Match getMatchFromId(int id){return matchdata.get(id);}
    public static Team getTeamFromId(int id){return team.get(id);}
    public static Color getColor(String name){return colordata.get(name);}
    
    
    public static void setPlayerData(Player player, PlayerData data){playerdata.put(player, data);}
    public static void setMatch(int id, Match match){matchdata.put(id, match);}
    public static void setColor(String name, Color color){colordata.put(name, color);}
    
    public static Color getColorRandom(int number){
        list = new ArrayList<>(colordata.values());
        Collections.shuffle(list);
        Color color = list.get(number);
        color.setIsUsed(true);
        return color;   //RandomColor
    }
    
}
