
package be4rjp.sclat.data;

import org.bukkit.World;

/**
 *
 * @author Be4rJP
 */
public class Match {
    private int id;
    private World world;
    private Team team0;
    private Team team1;
    private int playercount = 0;
    
    public Match(int id){this.id = id;}
    
    public World getWorld(){return world;}
    
    public Team getTeam0(){return team0;}
    
    public Team getTeam1(){return team1;}
    
    public int getPlayerCount(){return playercount;}
    
    
    public void setTeam0(Team team){team0 = team;}
    
    public void setTeam1(Team team){team1 = team;}
    
    public void addPlayerCount(){playercount++;}
}
