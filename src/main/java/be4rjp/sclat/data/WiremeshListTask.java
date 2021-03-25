package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class WiremeshListTask {
    private List<Block> blockList = new ArrayList<>();
    private List<Wiremesh> wiremeshsList = new ArrayList<>();
    private Map<Block, BlockData> blockDataMap = new HashMap<>();
    
    private final Location firstPoint;
    private final Location secondPoint;
    
    public WiremeshListTask(Location firstLocation, Location secondLocation, boolean trapDoor, boolean ironBars, boolean fence){
        this.firstPoint = firstLocation;
        this.secondPoint = secondLocation;
        
        
        //先に対象のブロックとそのBlockDataを取得して保存しておく
        List<Block> list = new RegionBlocks(firstPoint, secondPoint).getBlocks();
        
        for(Block block : list){
            if(!blockList.contains(block) && ((block.getType().equals(Material.IRON_TRAPDOOR) && trapDoor) || (block.getType().equals(Material.IRON_BARS) && ironBars) || (block.getType().toString().contains("FENCE") && fence))){
                BlockData bData = block.getBlockData();
                blockDataMap.put(block, bData);
                blockList.add(block);
            }
        }

        //Wiremeshを作成してタスクを実行
        for(Block block : blockList){
            BlockData bData = blockDataMap.get(block);
            Wiremesh wm = new Wiremesh(block, block.getType(), bData);
            wiremeshsList.add(wm);
        }
    }
    
    public List<Wiremesh> getWiremeshsList(){return this.wiremeshsList;}
    
    public void stopTask(){wiremeshsList.forEach((wm) -> {wm.stopTask();});}
}
