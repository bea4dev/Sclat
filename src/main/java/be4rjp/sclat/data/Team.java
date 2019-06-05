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
    
    public Team(int id){this.teamid = id;}
    
    public Color getTeamColor(){return teamcolor;}
    
    public void addPaintCount(){paintcount++;}
    
    public void subtractPaintCount(){paintcount--;}
    
    public void setTeamColor(Color color){teamcolor = color;}
    

}
