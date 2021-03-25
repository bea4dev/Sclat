
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sclat;
import be4rjp.sclat.ServerType;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.MainWeapon;
import be4rjp.sclat.data.Match;
import be4rjp.sclat.data.PaintData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Sponge;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.weapon.Gear;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class PaintMgr {
    public static void Paint(Location location, Player player, boolean sphere){
        
        if(Main.type == ServerType.LOBBY) return;
        
        be4rjp.sclat.data.MainWeapon mw = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon();
        List<Block> blocks = new ArrayList<Block>();
        blocks.add(location.getBlock());
        if(sphere)
            blocks = generateSphere(location, mw.getMaxPaintDis(), 1, false, true, 0, mw.getPaintRandom());
        //List<Block> blocks = getTargetBlocks(location, mw.getPaintRandom(), true, 0, mw.getMaxPaintDis());
        
        for(Block block : blocks) {
            
            if(block.getType() == DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool())
                continue;
            
            try {
                if (block.getY() <= DataMgr.getPlayerData(player).getMatch().getMapData().getVoidY()) {
                    continue;
                }
            }catch (Exception e){}
            
            if(block.getType().equals(Material.WET_SPONGE) || block.getType().toString().contains("POWDER")){
                if(DataMgr.getSpongeMap().containsKey(block)){
                    Sponge sponge = DataMgr.getSpongeFromBlock(block);
                    PlayerData pdata = DataMgr.getPlayerData(player);
                    if(pdata.getWeaponClass().getMainWeapon().getWeaponType().equals("Charger"))
                        sponge.giveDamage(15, pdata.getTeam());
                    else
                        sponge.giveDamage(pdata.getWeaponClass().getMainWeapon().getDamage(), pdata.getTeam());
                }else if(block.getType().equals(Material.WET_SPONGE)){
                    Sponge sponge = new Sponge(block);
                    PlayerData pdata = DataMgr.getPlayerData(player);
                    sponge.setMatch(pdata.getMatch());
                    sponge.setTeam(pdata.getTeam());
                    DataMgr.setSpongeWithBlock(block, sponge);
                    sponge.giveDamage(pdata.getWeaponClass().getMainWeapon().getDamage(), pdata.getTeam());
                }
            }
            
            if(block.getType().equals(Material.WET_SPONGE) || block.getType().toString().contains("POWDER"))
                return;
            
            if(canPaint(block)){
                if(!conf.getConfig().getString("WorkMode").equals("Trial"))
                    if(!DataMgr.getPlayerData(player).getMatch().getMapData().canPaintBBlock() && block.getType() == Material.BARRIER)
                        return;
                if(DataMgr.getBlockDataMap().containsKey(block)){
                    PaintData data = DataMgr.getPaintDataFromBlock(block);
                    Team BTeam = data.getTeam();
                    Team ATeam = DataMgr.getPlayerData(player).getTeam();
                    if(BTeam != ATeam){
                        data.setTeam(ATeam);
                        BTeam.subtractPaintCount();
                        ATeam.addPaintCount();
                        //Sclat.setBlockByNMS(block, ATeam.getTeamColor().getWool(), false);
                        DataMgr.getPlayerData(player).getMatch().getBlockUpdater().setBlock(block, ATeam.getTeamColor().getWool());
                        DataMgr.getPlayerData(player).addPaintCount();
                        if(new Random().nextInt((int)(12 / Gear.getGearInfluence(player, Gear.Type.SPECIAL_UP) / DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getSPRate())) == 0 && !DataMgr.getPlayerData(player).getIsUsingSP())
                            SPWeaponMgr.addSPCharge(player);
                    }
                }else{
                    Team team = DataMgr.getPlayerData(player).getTeam();
                    team.addPaintCount(); 
                    PaintData data = new PaintData(block);
                    data.setMatch(DataMgr.getPlayerData(player).getMatch());
                    data.setTeam(team);
                    data.setOrigianlType(block.getType());
                    data.setBlockData(block.getBlockData());
                    //data.setOriginalState(block.getState());
                    DataMgr.setPaintDataFromBlock(block, data);
                    //Sclat.setBlockByNMS(block, team.getTeamColor().getWool(), false);
                    DataMgr.getPlayerData(player).getMatch().getBlockUpdater().setBlock(block, team.getTeamColor().getWool());
                    DataMgr.getPlayerData(player).addPaintCount();
                    if(new Random().nextInt((int)(13 / Gear.getGearInfluence(player, Gear.Type.SPECIAL_UP) / DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getSPRate())) == 0 && !DataMgr.getPlayerData(player).getIsUsingSP())
                        SPWeaponMgr.addSPCharge(player);
                }
                
                
            }
        }
    }
    
    public static boolean canPaint(Block block){
        return !(block.getType() == Material.AIR || block.getType() == Material.SHULKER_BOX || block.getType() == Material.IRON_BARS || block.getType() == Material.VINE || block.getType().toString().contains("SIGN") || block.getType().toString().contains("GLASS") || block.getType().toString().contains("CARPET") || block.getType().toString().contains("POWDER") || block.getType().toString().contains("FENCE") || block.getType().toString().contains("STAIR") || block.getType().toString().contains("PLATE") || block.getType() == Material.WATER || block.getType() == Material.OBSIDIAN || block.getType().toString().contains("SLAB") || block.getType().toString().contains("DOOR"));
    }
    
    
    public static void PaintByTeam(Block block, Team team, Match match){  
        if(block.getType() == team.getTeamColor().getWool())
            return;

        if(!(block.getType() == Material.AIR || block.getType() == Material.SHULKER_BOX || block.getType() == Material.IRON_BARS || block.getType() == Material.VINE || block.getType().toString().contains("SIGN") || block.getType().toString().contains("GLASS") || block.getType().toString().contains("CARPET") || block.getType().toString().contains("POWDER") || block.getType().toString().contains("FENCE") || block.getType().toString().contains("STAIR") || block.getType().toString().contains("PLATE") || block.getType() == Material.WATER || block.getType() == Material.OBSIDIAN || block.getType().toString().contains("SLAB"))){
            if(DataMgr.getBlockDataMap().containsKey(block)){
                PaintData data = DataMgr.getPaintDataFromBlock(block);
                Team BTeam = data.getTeam();
                Team ATeam = team;
                if(BTeam != ATeam){
                    data.setTeam(ATeam);
                    BTeam.subtractPaintCount();
                    ATeam.addPaintCount();
                    match.getBlockUpdater().setBlock(block, ATeam.getTeamColor().getWool());
                }
            }else{
                team.addPaintCount(); 
                PaintData data = new PaintData(block);
                data.setMatch(match);
                data.setTeam(team);
                data.setOrigianlType(block.getType());
                data.setBlockData(block.getBlockData());
                DataMgr.setPaintDataFromBlock(block, data);
                match.getBlockUpdater().setBlock(block, team.getTeamColor().getWool());
            }
        }
    }
    
    
    public static ArrayList<Block> getCubeBlocks(Block start, int radius){
        ArrayList<Block> blocks = new ArrayList<Block>();
        for(double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++){
            for(double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++){
                for(double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++){
                    Location loc = new Location(start.getWorld(), x, y, z);
                    blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;
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
    
    public static synchronized List<Block> generateSphere(Location loc, double r, double h, boolean hollow, boolean sphere, double plus_y, int random) {
        List<Block> circleblocks = new ArrayList<Block>();
        double cx = loc.getX();
        double cy = loc.getY();
        double cz = loc.getZ();
        
        int i = 0;
        
        for (double x = cx - r; x <= cx + r; x++) {
            for (double z = cz - r; z <= cz + r; z++) {
                for (double y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l.getWorld().getBlockAt(l));
                        if(random < i){
                            Random ran = new Random();
                            int rani = ran.nextInt(2);
                            if(rani == 0)
                                return circleblocks;
                        }
                        i++;
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
                        Paint(new Location(player.getWorld(), x, i, z), player, true);
                        
                    }
                    break;
                }
                i--;
            }
            
        }
    }
    
    public static void PaintHightestBlock(Location loc, Player player, boolean randomb, boolean inkrandom){
        int i = loc.getBlockY();
        int x = loc.getBlockX();
        int z = loc.getBlockZ();
            while(i>0){
                if(new Location(player.getLocation().getWorld(), x, i, z).getBlock().getType() != Material.AIR){
                    Random random = new Random();
                    int r = random.nextInt(8);
                    if(r == 0 && randomb){
                        Paint(new Location(player.getLocation().getWorld(), x, i, z), player, inkrandom);
                    }
                    if(!randomb)
                        Paint(new Location(player.getLocation().getWorld(), x, i, z), player, inkrandom);
                    break;
                }
                i--;
            }
    }
    
    public static void PaintGlass(Match match){
        //team0
        List<Block> blocks = new ArrayList<Block>();
        Block b0 = match.getMapData().getTeam0Loc().getBlock().getRelative(BlockFace.DOWN);
        blocks.add(b0);
        blocks.add(b0.getRelative(BlockFace.EAST));
        blocks.add(b0.getRelative(BlockFace.NORTH));
        blocks.add(b0.getRelative(BlockFace.SOUTH));
        blocks.add(b0.getRelative(BlockFace.WEST));
        blocks.add(b0.getRelative(BlockFace.NORTH_EAST));
        blocks.add(b0.getRelative(BlockFace.NORTH_WEST));
        blocks.add(b0.getRelative(BlockFace.SOUTH_EAST));
        blocks.add(b0.getRelative(BlockFace.SOUTH_WEST));
        for(Block block : blocks) {
            if(block.getType().equals(Material.WHITE_STAINED_GLASS)){
                PaintData data = new PaintData(block);
                data.setMatch(match);
                data.setTeam(match.getTeam0());
                data.setOrigianlType(block.getType());
                DataMgr.setPaintDataFromBlock(block, data);
                Sclat.setBlockByNMS(block, match.getTeam0().getTeamColor().getGlass(), false);
                //block.setType(match.getTeam0().getTeamColor().getGlass());
                match.getTeam0().addPaintCount();
            }
        }
        
        //team1
        List<Block> blocks1 = new ArrayList<Block>();
        Block b1 = match.getMapData().getTeam1Loc().getBlock().getRelative(BlockFace.DOWN);
        blocks1.add(b1);
        blocks1.add(b1.getRelative(BlockFace.EAST));
        blocks1.add(b1.getRelative(BlockFace.NORTH));
        blocks1.add(b1.getRelative(BlockFace.SOUTH));
        blocks1.add(b1.getRelative(BlockFace.WEST));
        blocks1.add(b1.getRelative(BlockFace.NORTH_EAST));
        blocks1.add(b1.getRelative(BlockFace.NORTH_WEST));
        blocks1.add(b1.getRelative(BlockFace.SOUTH_EAST));
        blocks1.add(b1.getRelative(BlockFace.SOUTH_WEST));
        for(Block block : blocks1) {
            if(block.getType().equals(Material.WHITE_STAINED_GLASS)){
                PaintData data = new PaintData(block);
                data.setMatch(match);
                data.setTeam(match.getTeam1());
                data.setOrigianlType(block.getType());
                DataMgr.setPaintDataFromBlock(block, data);
                Sclat.setBlockByNMS(block, match.getTeam1().getTeamColor().getGlass(), false);
                //block.setType(match.getTeam1().getTeamColor().getGlass());
                match.getTeam1().addPaintCount();
            }
        }
    }
    
}
