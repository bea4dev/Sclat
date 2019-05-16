package be4rjp.sclat.data;

import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PlayerData {
    private Player player;
    private Match match;
    private Team team;
    private boolean inmatch = false;
    
   public PlayerData(Player player){this.player = player;}
   
   public Match getMatch(){return match;}
   
   public Team getTeam(){return team;}
   
   public boolean isInMatch(){return inmatch;}
    
    
    
}
