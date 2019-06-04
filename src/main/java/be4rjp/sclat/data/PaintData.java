
package be4rjp.sclat.data;

import org.bukkit.block.Block;

/**
 *
 * @author Be4rJP
 */
public class PaintData {
    
    private Block paintedblock;
    
    private Match match;
    
    public PaintData(Block block){this.paintedblock = block;}
    
    public Match getMatch(){return this.match;}
    
    public void setMatch(Match match){this.match = match;}
}
