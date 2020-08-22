package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Wiremesh {
    private final Block block;
    private final Material originalType;
    private FallingBlock fb;
    private final BukkitRunnable task;
    private final BlockData blockData;
    
    private boolean despawn = true;
    private boolean spawn = false;
    
    public Wiremesh(Block b, Material origType, BlockData bData){
        this.block = b;
        this.originalType = origType;
        this.blockData = bData;
        
        
        block.setType(Material.AIR);
        
        Location loc = block.getLocation().clone().add(0.5, 0, 0.5);
        fb = block.getWorld().spawnFallingBlock(loc, blockData);
        fb.setDropItem(false);
        fb.setHurtEntities(false);
        fb.setGravity(false);
        fb.setTicksLived(1);
        
        
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                try{
                    for (Player player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(block.getWorld() == player.getWorld()){
                            
                            //透過条件
                            boolean is = player.getInventory().getItemInMainHand().getType().equals(Material.AIR);
                            
                            if(block.getLocation().distance(player.getLocation()) <= 5 && !is){
                                player.sendBlockChange(block.getLocation(), blockData);
                            }else{
                                if(block.getLocation().distance(player.getLocation()) <= 5 && is)
                                    player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
                                else
                                    player.sendBlockChange(block.getLocation(), blockData);
                            }
                            
                            
                            if(block.getLocation().distance(player.getLocation()) >= 5){
                                if(despawn){
                                    fb.remove();
                                    despawn = false;
                                    spawn = true;
                                }
                            }
                            
                            if(block.getLocation().distance(player.getLocation()) <= 5){
                                if(spawn && is){
                                    fb = block.getWorld().spawnFallingBlock(loc, blockData);
                                    fb.setDropItem(false);
                                    fb.setHurtEntities(false);
                                    fb.setGravity(false);
                                    fb.setTicksLived(1);
                                    despawn = true;
                                    spawn = false;
                                }
                            }
                        }
                    }
                    
                    //Falling block death cancel
                    fb.setTicksLived(1);
                }catch(Exception e){cancel();}
            }
        };
        
        startTask();
    }
    
    private void startTask(){this.task.runTaskTimer(Main.getPlugin(), 0, 1);}
    
    public void stopTask(){
        this.task.cancel();
        this.block.setType(originalType);
        this.block.setBlockData(blockData);
        if(this.fb != null)
            this.fb.remove();
    }
}
