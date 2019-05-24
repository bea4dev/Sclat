package be4rjp.sclat.data;

/**
 *
 * @author Be4rJP
 */
public class Team {
    private int teamid;
    private Color teamcolor;
    private String mapname;
    
    public Team(int id){this.teamid = id;}
    
    public Color getTeamColor(){return teamcolor;}
    
    
    
    public void setTeamColor(Color color){teamcolor = color;}
    

}
