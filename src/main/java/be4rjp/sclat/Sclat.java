package be4rjp.sclat;

import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import net.minecraft.server.v1_13_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 * 
 * 全体的に使いそうなメソッドをここに置いておく
 * 
 */

public class Sclat {
    
    public static void setBlockByNMS(org.bukkit.block.Block b, org.bukkit.Material material, boolean applyPhysics) {
        Location loc = b.getLocation();
        Block block = ((CraftBlockData) Bukkit.createBlockData(material)).getState().getBlock();
        net.minecraft.server.v1_13_R1.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(loc.getX(), loc.getY(), loc.getZ());
        IBlockData ibd = block.getBlockData();
        nmsWorld.setTypeAndData(bp, ibd, applyPhysics ? 3 : 2);
    }
    
    /*
    public static void setBlockByNMS(org.bukkit.block.Block b, org.bukkit.Material material, boolean applyPhysics) {
        Location loc = b.getLocation();
        Block block = ((CraftBlockData) Bukkit.createBlockData(material)).getState().getBlock();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        net.minecraft.server.v1_13_R1.World nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.server.v1_13_R1.Chunk nmsChunk = nmsWorld.getChunkAt(x >> 4, z >> 4);
        ChunkSection cs = nmsChunk.getSections()[y >> 4];
        IBlockData ibd = block.getBlockData();
        if (cs == nmsChunk.a()) {
            cs = new ChunkSection(y >> 4 << 4, false);
            nmsChunk.getSections()[y >> 4] = cs;
        }

        cs.getBlocks().setBlock(x & 15, y & 15, z & 15, ibd);
    }*/
    
    public static void setPlayerPrefix(Player player, String prefix) {
        String name = prefix + player.getDisplayName();
        CraftPlayer cp = (CraftPlayer)player;
        EntityPlayer ep = cp.getHandle();
        String origName = ep.getName();
        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
            if(player != target){
                CraftPlayer ct = (CraftPlayer)player;
                ct.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(ep));
            }
        }
        
    }
}
