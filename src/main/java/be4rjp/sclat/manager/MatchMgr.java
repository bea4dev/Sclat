
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
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
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.isInMatch()){
            player.sendMessage("§c§n既にチームに参加しています");
            System.exit(0);
        }
        Match match = DataMgr.getMatchFromId(matchcount);
        match.addPlayerCount();
        int playercount = match.getPlayerCount();
        if(playercount <= 8){
            if(playercount%2==0){
                data.setTeam(match.getTeam1());
            }else{
                data.setTeam(match.getTeam0());
            }
            data.setIsInMatch(true);
            data.setMatch(match);
            if(playercount == 1){//-------------------test--------------------//
                StartMatch(match);
            }
        }else{
            player.sendMessage("§c§n上限人数を超えているため参加できません");
        }
        
        
    }
    
    public static void MatchSetup(){
        int id = matchcount;
        Match match = new Match(id);
        DataMgr.setMatch(id, match);
        Team team0 = new Team(id * 2);
        Team team1 = new Team(id * 2 + 1);
        DataMgr.setTeam(id * 2, team0);
        DataMgr.setTeam(id * 2 + 1, team1);
        match.setTeam0(team0);
        match.setTeam1(team1);
        
        Color color0 = DataMgr.getColorRandom(0);
        Color color1 = DataMgr.getColorRandom(1);
        team0.setTeamColor(color0);
        team1.setTeamColor(color1);
    }
    
    public static void StartMatch(Match match){
        for(Player player : Main.getPlugin(Main.class).getServer().getOnlinePlayers()){
            PlayerData data = DataMgr.getPlayerData(player);
            if(data.getMatch() == match){
                player.sendTitle("","§a試合開始まで後10秒", 10, 70, 20);
                
            }
        }
    }
}
