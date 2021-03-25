
package be4rjp.sclat.listener;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.Team;
import be4rjp.sclat.manager.DeathMgr;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

/**
 *
 * @author Be4rJP
 */
public class SquidListenerMgr {
    public static void CheckOnInk(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        if(!data.isInMatch())
            return;
        Location loc = player.getLocation();
        Block playerblock = player.getLocation().getBlock();
        Block b1 = loc.add(0, -0.5, 0).getBlock();
        Block b2 = player.getLocation().getBlock().getRelative(BlockFace.NORTH);
        Block b3 = player.getLocation().getBlock().getRelative(BlockFace.EAST);
        Block b4 = player.getLocation().getBlock().getRelative(BlockFace.SOUTH);
        Block b5 = player.getLocation().getBlock().getRelative(BlockFace.WEST);
        
        List<Block> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        list.add(b3);
        list.add(b4);
        list.add(b5);
        
        if(playerblock.getType() == Material.WATER && player.getGameMode().equals(GameMode.ADVENTURE))
            DeathMgr.PlayerDeathRunnable(player, player, "water");
        
        try{
            if(data.getMatch().getMapData().getVoidY() != 0) {
                if (player.getLocation().getY() <= data.getMatch().getMapData().getVoidY()) {
                    DeathMgr.PlayerDeathRunnable(player, player, "fall");
                }
            }
        }catch (Exception e){}
        
        //if(!DataMgr.getBlockDataMap().containsKey(b2) || !DataMgr.getBlockDataMap().containsKey(b3) || !DataMgr.getBlockDataMap().containsKey(b4) || !DataMgr.getBlockDataMap().containsKey(b5) || !DataMgr.getBlockDataMap().containsKey(b1))
            //return;
        
        Team team = DataMgr.getPlayerData(player).getTeam();
        Material p = Material.getMaterial(data.getTeam().getTeamColor().getConcrete().toString() + "_POWDER");
        
        for(Block block : list){
            if(!block.equals(b1)){
                if(DataMgr.getBlockDataMap().containsKey(block)){
                    if(DataMgr.getBlockDataMap().get(block).getTeam() == data.getTeam()){
                        if(!data.getIsSquid() || block.getType().equals(Material.AIR))
                            continue;
                        data.setIsOnInk(true);
                        player.setAllowFlight(true);
                        player.setFlying(true);
                        return;
                    }
                }
            }else{
                if(DataMgr.getBlockDataMap().containsKey(block)){
                    if(DataMgr.getBlockDataMap().get(block).getTeam() == data.getTeam()){
                        if(!data.getIsSquid() || block.getType().equals(Material.AIR))
                            continue;
                        data.setIsOnInk(true);
                        if(!data.getIsUsingJetPack()){
                            player.setAllowFlight(false);
                            player.setFlying(false);
                        }
                        return;
                    }
                }
            }
        }
            
        data.setIsOnInk(false);
        if(!data.getIsUsingJetPack()){
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }
}
