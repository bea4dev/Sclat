
package be4rjp.sclat.manager;

import org.bukkit.entity.Player;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;

/**
 *
 * @author Be4rJP
 */
public class MatchMgr {
    
    public static int matchidcount = 0;
    
    public static void PlayerJoinMatch(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(data.isInMatch())
            return;
        
    }
    
    public static void TeamSetup(){
        
    }
}
