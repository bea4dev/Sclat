package be4rjp.sclat.data;

import be4rjp.sclat.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Wiremesh {
    private final Block block;
    private final Material originalType;
    private FallingBlock fb;
    private ArmorStand as;
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
        fb = block.getWorld().spawnFallingBlock(loc.clone().add(0, 0, 0), blockData);
        fb.setDropItem(false);
        fb.setHurtEntities(false);
        fb.setGravity(false);
        fb.setTicksLived(1);
        //((LivingEntity)fb).setCollidable(false);
        
        
        as = block.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setMarker(true);
        });
        
        as.addPassenger(fb);
        
        
        this.task = new BukkitRunnable() {
            
            @Override
            public void run() {
                try{
                    boolean near = false;
                    
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
                            
                            if(block.getLocation().distance(player.getLocation()) <= 5)
                                near = true;
                        }
                    }
                    
                    //一人でも近いプレイヤーがいればFallingBlockをスポーンさせる
                    if(near){
                        if(spawn){
                            fb = block.getWorld().spawnFallingBlock(loc.clone().add(0, 0, 0), blockData);
                            fb.setDropItem(false);
                            fb.setHurtEntities(false);
                            fb.setGravity(false);
                            fb.setTicksLived(1);
                            //((LivingEntity)fb).setCollidable(false);

                            
                            as = block.getWorld().spawn(loc, ArmorStand.class, armorStand -> {
                                armorStand.setVisible(false);
                                armorStand.setGravity(false);
                                armorStand.setMarker(true);
                            });
                            
                            as.addPassenger(fb);
                            

                            despawn = true;
                            spawn = false;
                        }
                    }else{
                        if(despawn){
                            fb.remove();
                            as.remove();
                            despawn = false;
                            spawn = true;
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
        if(this.as != null)
            this.as.remove();
    }
}
