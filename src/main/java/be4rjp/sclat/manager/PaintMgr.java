
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MainWeapon;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.PlayerData;
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
        //List<Block> blocks = generateSphere(location, 2, 1, false, true, 0);
        be4rjp.sclat.data.MainWeapon mw = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon();
        List<Block> blocks = getTargetBlocks(location, mw.getPaintRandom(), true, 0, mw.getMaxPaintDis());
        for(Block block : blocks) {
            if(!(block.getType() == Material.AIR || block.getType() == Material.IRON_BARS || block.getType().toString().contains("GLASS") || block.getType().toString().contains("FENCE") || block.getType().toString().contains("NETHER"))){
            if(DataMgr.getBlockDataMap().containsKey(block)){
                PaintData data = DataMgr.getPaintDataFromBlock(block);
                Team BTeam = data.getTeam();
                Team ATeam = DataMgr.getPlayerData(player).getTeam();
                if(BTeam != ATeam){
                data.setTeam(ATeam);
                BTeam.subtractPaintCount();
                ATeam.addPaintCount();
                block.setType(ATeam.getTeamColor().getWool());
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                block.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, block.getLocation(), 5, 0.5, 0.5, 0.5, 1, bd);
                }
            }else{
                Team team = DataMgr.getPlayerData(player).getTeam();
                team.addPaintCount(); 
                PaintData data = new PaintData(block);
                data.setMatch(DataMgr.getPlayerData(player).getMatch());
                data.setTeam(team);
                data.setOrigianlType(block.getType());
                //data.setOriginalState(block.getState());
                DataMgr.setPaintDataFromBlock(block, data);
                block.setType(team.getTeamColor().getWool());
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                block.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, block.getLocation(), 5, 0.5, 0.5, 0.5, 1, bd);
            }
            }
        }
    }
    
    
    public static synchronized List<Block> getTargetBlocks(Location loc, int r, boolean loop, int loopc, int max)
    {
        
        Block b0 = loc.getBlock();
        Block b1 = loc.add(1, 0, 0).getBlock();
        Block b2 = loc.add(0, 0, 1).getBlock();
        Block b3 = loc.add(-1, 0, 0).getBlock();
        Block b4 = loc.add(0, 0, -1).getBlock();
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
            if(c == 0)
                tempList.addAll(getTargetBlocks(b1.getLocation(), r, false, loopc2, max));
            if(c == 1)
                tempList.addAll(getTargetBlocks(b2.getLocation(), r, false, loopc2, max));
            if(c == 2)
                tempList.addAll(getTargetBlocks(b3.getLocation(), r, false, loopc2, max));
            if(c == 3)
                tempList.addAll(getTargetBlocks(b4.getLocation(), r, false, loopc2, max));
            
            loopc2++;
            
            tempList.addAll(getTargetBlocks(b5.getLocation(), r, false, loopc2, max));
            tempList.addAll(getTargetBlocks(b6.getLocation(), r, false, loopc2, max));
        }
        return tempList;
        
    }
    
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
    
    public static void PaintInLine(Location l1, Location l2, Player player){
        double xSlope = (l1.getBlockX() - l2.getBlockX());
        double ySlope = (l1.getBlockY() - l2.getBlockY()) / xSlope;
        double zSlope = (l1.getBlockZ() - l2.getBlockZ()) / xSlope;
        double y = l1.getBlockY();
        double z = l1.getBlockZ();
        double interval = 1 / (Math.abs(ySlope) > Math.abs(zSlope) ? ySlope : zSlope);
        for (double x = l1.getBlockX(); x - l1.getBlockX() < Math.abs(xSlope); x += interval, y += ySlope, z += zSlope) {
            int i = (int)y;
            while(i>0){
                if(new Location(player.getWorld(), x, i, z).getBlock().getType() != Material.AIR){
                    Random random = new Random();
                    int r = random.nextInt(10);
                    if(r == 0){
                        Paint(new Location(player.getWorld(), x, i, z), player);
                        
                    }
                    break;
                }
                i--;
            }
            
        }
    }
    
    public static void PaintHightestBlock(Location loc, Player player){
        int i = loc.getBlockY();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
            while(i>0){
                if(new Location(player.getWorld(), x, i, z).getBlock().getType() != Material.AIR){
                    Random random = new Random();
                    int r = random.nextInt(10);
                    if(r == 0){
                        Paint(new Location(player.getWorld(), x, i, z), player);
                    }
                    break;
                }
                i--;
            }
    }
    
    
}
