package be4rjp.sclat.lunachat;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import com.github.ucchyocean.lc3.bukkit.event.LunaChatBukkitPreChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author Be4rJP
 */
public class LunaChatListener implements Listener{
    //@EventHandler
    public void onChat(LunaChatBukkitPreChatEvent event){
        
        Player sender = null;
        for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
            if(player.getName().equals(event.getMember().getName())){
                sender = player;
            }
        }
        if(sender != null){
            PlayerData data = DataMgr.getPlayerData(sender);
            if(data.getIsJoined())
                event.setMessage(data.getTeam().getTeamColor().getColorCode() + event.getMessage());
        }
        
    }
}
