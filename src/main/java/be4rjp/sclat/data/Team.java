package be4rjp.sclat.data;

/**
 *
 * @author Be4rJP
 */
public class Team {
    private int teamid;
    private Color teamcolor;
    private String mapname;
    private int paintcount = 0;
    private int killcount = 0;
    
    public Team(int id){this.teamid = id;}
    
    public int getID(){return this.teamid;}
    
    public Color getTeamColor(){return teamcolor;}
    
    public int getPoint(){return this.paintcount;}
    
    public int getKillCount(){return this.killcount;}
    
    public void addPaintCount(){paintcount++;}
    
    public void subtractPaintCount(){paintcount--;}
    
    public void addKillCount(){killcount++;}
    
    public void setTeamColor(Color color){teamcolor = color;}
    

}
