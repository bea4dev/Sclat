
package be4rjp.sclat.data;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Be4rJP
 */
public class PaintData {
    
    private Block paintedblock;
    
    private Match match;
    
    private Material origtype;
    
    private Team team;
    
    public PaintData(Block block){this.paintedblock = block;}
    
    public Match getMatch(){return this.match;}
    
    public Block getBlock(){return this.paintedblock;}
    
    public Material getOriginalType(){return this.origtype;}
    
    public Team getTeam(){return this.team;}
    
    public void setMatch(Match match){this.match = match;}
    
    public void setOrigianlType(Material material){this.origtype = material;}
    
    public void setTeam(Team team){this.team = team;}
}
