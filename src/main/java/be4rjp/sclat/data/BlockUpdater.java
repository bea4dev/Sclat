
package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sclat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_14_R1.PacketPlayOutMultiBlockChange;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_14_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
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
                try {
                    List<Block> tb = blocks.subList(c, blocks.size());
                    
                    Map<Chunk, List<Block>> chunkBlockMap = new HashMap<>();
                    
                    loop:
                    for (Block block : tb) {
                        //Sclat.setBlockByNMS(block, blocklist.get(block), true);
                        if (block.getLocation().getChunk().isLoaded()) {
                            try {
                                //Sclat.setBlockByNMSChunk(block, blocklist.get(block), true);
                
                                List<Block> list = new ArrayList<>();
                                Block up = block.getRelative(BlockFace.UP);
                                Block west = block.getRelative(BlockFace.WEST);
                                Block east = block.getRelative(BlockFace.EAST);
                                Block south = block.getRelative(BlockFace.SOUTH);
                                Block north = block.getRelative(BlockFace.NORTH);
                                Block down = block.getRelative(BlockFace.DOWN);
                                list.add(up);
                                list.add(west);
                                list.add(east);
                                list.add(south);
                                list.add(north);
                                list.add(down);
                
                                check:
                                for (Block cb : list) {
                                    if (cb.getType().equals(Material.AIR)) {
                                        //Sclat.sendBlockChangeForAllPlayer(block, blocklist.get(block));
                                        chunkBlockMap.computeIfAbsent(block.getChunk(), chunk -> new ArrayList<>()).add(block);
                                        continue check;
                                    }
                                }
                            } catch (Exception e) {
                            }
                        } else {
                        }
                        //block.setType(blocklist.get(block));
                        c++;
                        i++;
                        if (i == maxBlock) {
                            i = 0;
                            break;
                        }
                    }
                    
                    //Use multi block change
                    for (Map.Entry<Chunk, List<Block>> entry : chunkBlockMap.entrySet()){
                        Chunk chunk = entry.getKey();
                        List<Block> blocks = entry.getValue();
                        
                        short[] positionArray = new short[blocks.size()];
                        int i = 0;
                        for(Block block : blocks){
                            positionArray[i] = (short) ((block.getX() & 0xF) << 12 | (block.getZ() & 0xF) << 8 | block.getY());
                            i++;
                        }
                        PacketPlayOutMultiBlockChange packet = new PacketPlayOutMultiBlockChange(positionArray.length, positionArray, ((CraftChunk) chunk).getHandle());
                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(target.getWorld() == chunk.getWorld()){
                                ((CraftPlayer)target).getHandle().playerConnection.sendPacket(packet);
                            }
                        }
                    }
                    
                }catch (Exception e){cancel();}
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
