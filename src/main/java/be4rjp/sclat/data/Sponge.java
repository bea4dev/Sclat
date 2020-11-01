package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.manager.PaintMgr;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Sponge {
    private Block block;
    private Team team = null;
    private double hp = 1;
    private Match match;
    private int level = 0;
    private boolean canGiveDamage = true;
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
        
        if(!canGiveDamage)
            return;
        canGiveDamage = false;
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                canGiveDamage = true;
            }
        };
        task.runTaskLater(Main.getPlugin(), 10);
                
        if(this.team != team){
            if(this.hp > damage){
                this.hp -= damage;
            }else{
                this.team = team;
                this.hp = 1;
            }
        }else{
            if(this.hp < 30){
                this.hp += damage;
            }
        }
        
        if(this.hp <= 10){
            if(this.level != 0)
                this.block.getLocation().getWorld().playSound(this.block.getLocation(), Sound.ITEM_BUCKET_FILL, 1F, 1F);
            this.level = 0;
        }
            
        if(this.hp > 10 && this.hp < 25){
            if(this.level != 1)
                this.block.getLocation().getWorld().playSound(this.block.getLocation(), Sound.ITEM_BUCKET_FILL, 1F, 1F);
            this.level = 1;
        }
        if(this.hp >= 25){
            if(this.level != 2)
                this.block.getLocation().getWorld().playSound(this.block.getLocation(), Sound.ITEM_BUCKET_FILL, 1F, 1F);
            this.level = 2;
        }
        
        //プレイヤーが近くにいないかチェック
        /*
        for (Player player : Main.getPlugin().getServer().getOnlinePlayers()) {
            if(player.getWorld() == this.block.getWorld())
                if(player.getLocation().distance(this.block.getLocation()) < 3)
                    return;
        }
        */
        
        //Block reset
        List<Block> rb = PaintMgr.getCubeBlocks(block, 2);
        for(Block b : rb) {
            if(b.getType().equals(Material.AIR) || b.getType().toString().contains("POWDER")){
                if(DataMgr.getBlockDataMap().containsKey(b)){
                    PaintData data = DataMgr.getPaintDataFromBlock(b);
                    data.setTeam(this.team);
                    //match.getBlockUpdater().setBlock(b, Material.AIR);
                    Sclat.setBlockByNMS(b, Material.AIR, true);
                    //b.setType(Material.AIR);
                }else{
                    PaintData data = new PaintData(b);
                    data.setMatch(match);
                    data.setOrigianlType(b.getType());
                    data.setTeam(this.team);
                    //match.getBlockUpdater().setBlock(b, Material.AIR);
                    Sclat.setBlockByNMS(b, Material.AIR, true);
                    //b.setType(Material.AIR);
                    DataMgr.setPaintDataFromBlock(b, data);
                    DataMgr.setSpongeWithBlock(b, this);
                }
            }
        }
        
        List<Block> blocks = PaintMgr.getCubeBlocks(block, level);
        for(Block b : blocks) {
            if(b.getType().equals(Material.AIR) || b.getType().toString().contains("POWDER")){
                PaintData data = DataMgr.getPaintDataFromBlock(b);
                data.setTeam(this.team);
                //match.getBlockUpdater().setBlock(b, Material.getMaterial(this.team.getTeamColor().getConcrete().toString() + "_POWDER"));
                Sclat.setBlockByNMS(b, Material.getMaterial(this.team.getTeamColor().getConcrete().toString() + "_POWDER"), false);
                //b.setType(Material.getMaterial(this.team.getTeamColor().getConcrete().toString() + "_POWDER"));
            }
        }
    }
}
