
package be4rjp.sclat.listener;

import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.DeathMgr;
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
        
        Material wool = data.getTeam().getTeamColor().getWool();
        if(b2.getType().equals(wool) || b3.getType().equals(wool) || b4.getType().equals(wool) || b5.getType().equals(wool)){
            if(!data.getIsSquid())
                return;
            data.setIsOnInk(true);
            player.setAllowFlight(true);
            player.setFlying(true);
            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
            player.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, player.getLocation(), 2, 0.1, 0.1, 0.1, 1, bd);
            return;
        }if(b1.getType().equals(wool)){
            if(!data.getIsSquid())
                return;
            data.setIsOnInk(true);
            player.setFlying(false);
            player.setAllowFlight(false);
            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
            player.getLocation().getWorld().spawnParticle(org.bukkit.Particle.BLOCK_DUST, player.getLocation(), 2, 0.1, 0.1, 0.1, 1, bd);
            return;
        }
            
            data.setIsOnInk(false);
            player.setAllowFlight(false);
            player.setFlying(false);
            if(playerblock.getType() == Material.WATER)
                DeathMgr.PlayerDeathRunnable(player, player, "water");
            return;
        
    }
}
