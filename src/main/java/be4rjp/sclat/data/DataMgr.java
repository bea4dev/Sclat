
package be4rjp.sclat.data;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class DataMgr {
    private static Map<Player, PlayerData> playerdata = new HashMap<>();
    private static Map<Integer, Match> match = new HashMap<>();
    private static Map<Integer, Team> team = new HashMap<>();
    
    public static PlayerData getPlayerData(Player player){return playerdata.get(player);}
    public static Match getMatchFromId(int id){return match.get(id);}
    public static Team getTeamFromId(int id){return team.get(id);}
}
