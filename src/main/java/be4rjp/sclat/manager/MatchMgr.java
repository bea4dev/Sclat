
package be4rjp.sclat.manager;

import be4rjp.sclat.data.Color;
import org.bukkit.entity.Player;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Team;

/**
 *
 * @author Be4rJP
 */
public class MatchMgr {
    
    public static int matchcount = 0;
    
    public static void PlayerJoinMatch(Player player){
        PlayerData data = new PlayerData(player);
        DataMgr.setPlayerData(player, data);
        
    }
    
    public static void MatchSetup(int id){
        Match match = new Match(id);
        DataMgr.setMatch(id, match);
        Team team0 = new Team(id);
        Team team1 = new Team(id+1);
        match.setTeam0(team0);
        match.setTeam1(team1);
        
        Color color0 = DataMgr.getColorRandom(0);
        Color color1 = DataMgr.getColorRandom(1);
        team0.setTeamColor(color0);
        team1.setTeamColor(color1);
        
    }
}
