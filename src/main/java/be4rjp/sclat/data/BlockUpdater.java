
package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class BlockUpdater {
    private Map<Block, Material> blocklist = new HashMap<>();
    private List<Block> blocks = new ArrayList<>();
    private BukkitRunnable task;
    private int maxBlock = 30;
            
    public BlockUpdater(){
        task = new BukkitRunnable(){
            int c = 0;
            int i = 0;
            @Override
            public void run(){
                List<Block> tb = blocks.subList(c, blocks.size());
                for(Block block : tb){
                    //Sclat.setBlockByNMS(block, blocklist.get(block), true);
                    if(block.getLocation().getChunk().isLoaded()){
                        try{
                            //Sclat.setBlockByNMSChunk(block, blocklist.get(block), true);
                            Sclat.sendBlockChangeForAllPlayer(block, blocklist.get(block));
                        }catch(Exception e){
                        }
                    }else{
                        try {
                            //Sclat.setBlockByNMS(block, blocklist.get(block), true);
                        } catch (Exception e) {
                        }
                    }
                    //block.setType(blocklist.get(block));
                    c++;
                    i++;
                    if(i == maxBlock){
                        i = 0;
                        break;
                    }
                }
            }
        };
    }
    
    public void setBlock(Block block, Material material){
        
        if(this.blocks.contains(block))
            if(this.blocklist.get(block).equals(material))
                return;
        
        if(!this.blocks.contains(block)){
            this.blocklist.put(block, material);
            this.blocks.add(block);
            
            if(block.getLocation().getChunk().isLoaded()){
                try{
                    Sclat.setBlockByNMSChunk(block, blocklist.get(block), true);
                }catch(Exception e){
                }
            }else{
                try {
                    Sclat.setBlockByNMS(block, blocklist.get(block), true);
                    //Main.getPlugin().getServer().broadcastMessage("ChangeBlockByNMS!!");
                } catch (Exception e) {
                }
            }
            
        }else{
            if(!this.blocklist.get(block).equals(material)){
                //this.blocklist.put(block, material);
                this.blocklist.replace(block, material);
                this.blocks.add(block);
                
                if(block.getLocation().getChunk().isLoaded()){
                    try{
                        Sclat.setBlockByNMSChunk(block, blocklist.get(block), true);
                    }catch(Exception e){
                    }
                }else{
                    try {
                        Sclat.setBlockByNMS(block, blocklist.get(block), true);
                        //Main.getPlugin().getServer().broadcastMessage("ChangeBlockByNMS!!");
                    } catch (Exception e) {
                    }
                }
                
            }
        }
    }
    
    public void start(){task.runTaskTimer(Main.getPlugin(), 0, 2);}
    
    public void stop(){task.cancel();}
    
    public void setMaxBlockInOneTick(int i){this.maxBlock = i;}
}
