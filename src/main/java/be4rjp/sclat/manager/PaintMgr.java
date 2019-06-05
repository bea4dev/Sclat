
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.Team;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class PaintMgr {
    public static void Paint(Location location, Player player){
        List<Block> blocks = generateSphere(location, 2, 1, false, true, 0);
        for(Block block : blocks) {
            if(!(block.getType() == Material.AIR || block.getType() == Material.IRON_BARS || block.getType().toString().contains("GLASS"))){
            if(DataMgr.getBlockDataMap().containsKey(block)){
                PaintData data = DataMgr.getPaintDataFromBlock(block);
                Team BTeam = data.getTeam();
                Team ATeam = DataMgr.getPlayerData(player).getTeam();
                if(BTeam == ATeam)
                    return;
                BTeam.subtractPaintCount();
                ATeam.addPaintCount();
                block.setType(ATeam.getTeamColor().getWool());
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                block.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, block.getLocation(), 2, 0.1, 0.1, 0.1, 1, bd);
            }else{
                Team team = DataMgr.getPlayerData(player).getTeam();
                team.addPaintCount(); 
                PaintData data = new PaintData(block);
                data.setMatch(DataMgr.getPlayerData(player).getMatch());
                data.setTeam(team);
                data.setOrigianlType(block.getType());
                DataMgr.setPaintDataFromBlock(block, data);
                block.setType(team.getTeamColor().getWool());
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                block.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, block.getLocation(), 2, 0.1, 0.1, 0.1, 1, bd);
            }
            }
        }
    }
    
    /*
    public static synchronized List<Block> getTargetBlocks(Location loc, int r, boolean loop, int loopc, int max)
    {
        Main.getPlugin().getLogger().info("test");
        Block b0 = loc.getBlock();
        Block b1 = loc.add(1, 0, 0).getBlock();
        Block b2 = loc.add(1, 0, 1).getBlock();
        Block b3 = loc.add(-1, 0, 0).getBlock();
        Block b4 = loc.add(-1, 0, -1).getBlock();
        Block b5 = loc.add(0, 1, 0).getBlock();
        Block b6 = loc.add(0, -1, 0).getBlock();
        
        List<Block> tempList = new ArrayList<Block>();
        
        if(loopc == 0)
            tempList.add(b0);
        tempList.add(b1);
        tempList.add(b2);
        tempList.add(b3);
        tempList.add(b4);

        
        if(loop){
            Random random = new Random();
            int c = random.nextInt(r);
            boolean b = false;
            int loopc2 = loopc;
            if(c == 1 && loopc <= max){
                b = true;
            }
            loopc2++;
            tempList.addAll(getTargetBlocks(b1.getLocation(), r, b, loopc2, max));
            tempList.addAll(getTargetBlocks(b2.getLocation(), r, b, loopc2, max));
            tempList.addAll(getTargetBlocks(b3.getLocation(), r, b, loopc2, max));
            tempList.addAll(getTargetBlocks(b4.getLocation(), r, b, loopc2, max));
            tempList.addAll(getTargetBlocks(b5.getLocation(), r, b, loopc2, max));
            tempList.addAll(getTargetBlocks(b6.getLocation(), r, b, loopc2, max));
        }
        return tempList;
        
    }*/
    
    public static synchronized List<Block> generateSphere(Location loc, int r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<Block> circleblocks = new ArrayList<Block>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++) {
            for (int z = cz - r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l.getWorld().getBlockAt(l));
                    }
                }
            }
        }
        return circleblocks;
    }
    
    
    
}
