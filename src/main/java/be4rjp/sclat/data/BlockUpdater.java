
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
                    Sclat.setBlockByNMS(block, blocklist.get(block), true);
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
        this.blocklist.put(block, material);
        this.blocks.add(block);
    }
    
    public void start(){task.runTaskTimer(Main.getPlugin(), 0, 1);}
    
    public void stop(){task.cancel();}
    
    public void setMaxBlockInOneTick(int i){this.maxBlock = i;}
}
