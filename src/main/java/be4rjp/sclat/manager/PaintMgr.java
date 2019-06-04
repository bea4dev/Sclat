
package be4rjp.sclat.manager;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PaintData;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PaintMgr {
    public static void Paint(Location location, Player player){
        List<Block> blocks = getTargetBlocks(location, 2);
        for(Block block : blocks) {
            PaintData data = new PaintData(block);
            data.setMatch(DataMgr.getPlayerData(player).getMatch());
            
        }
    }
    
    public static synchronized List<Block> getTargetBlocks(Location l, int radius)
    {
        World w = l.getWorld();
        int xc = (int) l.getX();
        int zc = (int) l.getZ();
        int yc = (int) l.getY();
 
        List<Block> tempList = new ArrayList<Block>();
        for (int x = 0; x <= 2 * radius; x++)
        {
            for (int z = 0; z <= 2 * radius; z++)
            {
                for (int y = 0; y <= 2 * radius; y++)
                {
                    tempList.add(w.getBlockAt(xc + x, yc + y, zc + z));
                }
            }
        }
        return tempList;
    }
}
