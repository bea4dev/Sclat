package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.manager.PaintMgr;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Sponge {
    private Block block;
    private Team team = null;
    private double hp = 20;
    private Match match;
    //private BukkitRunnable task;
    
    public Sponge(Block block){
        this.block = block;
    }
    
    public Block getBlock(){return this.block;}
    
    public Team getTeam(){return this.team;}
    
    public double getHealth(){return this.hp;}
    
    public Match getMatch(){return this.match;}
    
    
    public void setBlock(Block block){this.block = block;}
    
    public void setTeam(Team team){this.team = team;}
    
    public void setHealth(double hp){this.hp = hp;}
    
    public void setMatch(Match match){this.match = match;}
    
    
    public void giveDamage(double damage, Team team){
        if(this.team != null){
            if(this.hp > damage){
                this.hp -= damage;
            }else{
                this.team = team;
                List<Block> blocks = new ArrayList<Block>();
                blocks = PaintMgr.getCubeBlocks(block, 2);
                for(Block b : blocks) {
                    if(b.getType().equals(Material.AIR) || b.getType().toString().contains("POWDER")){
                        if(DataMgr.getBlockDataMap().containsKey(b)){
                            PaintData data = DataMgr.getPaintDataFromBlock(b);
                            data.setTeam(team);
                            b.setType(Material.getMaterial(team.getTeamColor().getConcrete().toString() + "_POWDER"));
                        }else{
                            PaintData data = new PaintData(b);
                            data.setMatch(match);
                            data.setOrigianlType(b.getType());
                            data.setTeam(team);
                            b.setType(Material.getMaterial(team.getTeamColor().getConcrete().toString() + "_POWDER"));
                            DataMgr.setPaintDataFromBlock(b, data);
                            DataMgr.setSpongeWithBlock(b, this);
                        }
                    }
                }
            }
        }
    }
}
