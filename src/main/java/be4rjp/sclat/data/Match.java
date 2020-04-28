
package be4rjp.sclat.data;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
    private MapData map;
    private boolean canjoin = true;
    private Player leader;
    private int c_nawabari = 0;
    private int c_tdm = 0;
    private boolean finished = false;
    
    
    
    public Match(int id){this.id = id;}
    
    public World getWorld(){return world;}
    
    public Team getTeam0(){return team0;}
    
    public Team getTeam1(){return team1;}
    
    public int getPlayerCount(){return playercount;}
    
    public MapData getMapData(){return this.map;}
    
    public boolean canJoin(){return this.canjoin;}
    
    public Player getLeaderPlayer(){return this.leader;}
    
    public int getNawabari_T_Count(){return this.c_nawabari;}
    
    public int getTDM_T_Count(){return this.c_tdm;}
    
    public boolean isFinished(){return this.finished;}
    
    
    public void setTeam0(Team team){team0 = team;}
    
    public void setTeam1(Team team){team1 = team;}
    
    public void addPlayerCount(){playercount++;}
    
    public void setMapData(MapData map){this.map = map;}
    
    public void setCanJoin(boolean is){this.canjoin = is;}
    
    public void setLeaderPlayer(Player player){this.leader = player;}
    
    public void setIsFinished(boolean is){this.finished = is;}
    
    
    public void addNawabari_T_Count(){this.c_nawabari++;}
    
    public void addTDM_T_Count(){this.c_tdm++;}
}
