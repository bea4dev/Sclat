package be4rjp.sclat.data;

import java.util.*;

import be4rjp.sclat.Main;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Wiremesh extends BukkitRunnable{
    private final Block block;
    private final Material originalType;
    private EntityFallingBlock fb;
    private EntityArmorStand as;
    private final BlockData blockData;
    private final IBlockData ibd;
    
    private List<Player> playerList = new ArrayList<>();
    
    private boolean despawn = true;
    private boolean spawn = false;
    
    public Wiremesh(Block b, Material origType, BlockData bData){
        this.block = b;
        this.originalType = origType;
        this.blockData = bData;
        this.ibd = ((CraftBlockData) bData).getState();
        
        block.setType(Material.AIR);
        
        WorldServer nmsWorld = ((CraftWorld) block.getWorld()).getHandle();
        Location loc = block.getLocation();
        BlockData blockData = block.getBlockData();
        IBlockData ibd = ((CraftBlockData) blockData).getState();
        fb = new EntityFallingBlock(nmsWorld, loc.getX() + 0.5, loc.getY() - 0.02, loc.getZ() + 0.5, ibd);
        fb.setNoGravity(true);
        fb.ticksLived = 1;
        
        as = new EntityArmorStand(nmsWorld, loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5);
        as.setNoGravity(true);
        as.setMarker(true);
        as.setInvisible(true);
        fb.startRiding(as);
        
        for(Player player : Main.getPlugin().getServer().getOnlinePlayers()){
            if(block.getWorld() != player.getWorld()) continue;
            
            player.sendBlockChange(block.getLocation(), blockData);
        }
    }
    
    @Override
    public void run() {
        try{
            playerList.removeIf(player -> !player.isOnline());
            
            for (Player player : Main.getPlugin().getServer().getOnlinePlayers()) {
                if(block.getWorld() != player.getWorld()) continue;
                
                //透過条件
                boolean is = player.getInventory().getItemInMainHand().getType() == Material.AIR;
                
                EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
                
                if (block.getLocation().distanceSquared(player.getLocation()) <= 25 /* 5*5 */) {
                    
                    if(is){
                        player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
                    }else{
                        player.sendBlockChange(block.getLocation(), blockData);
                    }
                    
                    if (!playerList.contains(player)) {
                        PacketPlayOutSpawnEntity fbPacket = new PacketPlayOutSpawnEntity(fb, net.minecraft.server.v1_14_R1.Block.getCombinedId(ibd));
                        entityPlayer.playerConnection.sendPacket(fbPacket);
                        PacketPlayOutSpawnEntityLiving asPacket = new PacketPlayOutSpawnEntityLiving(as);
                        entityPlayer.playerConnection.sendPacket(asPacket);
                        DataWatcher dataWatcher = fb.getDataWatcher();
                        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(fb.getBukkitEntity().getEntityId(), dataWatcher, true);
                        entityPlayer.playerConnection.sendPacket(metadata);
                        PacketPlayOutMount mount = new PacketPlayOutMount(as);
                        entityPlayer.playerConnection.sendPacket(mount);
                        
                        playerList.add(player);
                    }
                } else {
                    
                    if(new Random().nextInt(5) == 0){
                        player.sendBlockChange(block.getLocation(), blockData);
                    }
                    
                    if (playerList.contains(player)) {
                        PacketPlayOutEntityDestroy fbPacket = new PacketPlayOutEntityDestroy(fb.getBukkitEntity().getEntityId());
                        entityPlayer.playerConnection.sendPacket(fbPacket);
                        PacketPlayOutEntityDestroy asPacket = new PacketPlayOutEntityDestroy(as.getBukkitEntity().getEntityId());
                        entityPlayer.playerConnection.sendPacket(asPacket);
                        player.sendBlockChange(block.getLocation(), blockData);
                        
                        playerList.remove(player);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void startTask(){
        this.runTaskTimerAsynchronously(Main.getPlugin(), 0, 5);
    }
    
    public void stopTask(){
        this.cancel();
        this.block.setType(originalType);
        this.block.setBlockData(blockData);
    }
}

