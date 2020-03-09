
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SuperArmor {
    public static void setArmor(Player player, double armor, long delay, boolean effect){
        
        PlayerData data = DataMgr.getPlayerData(player);
        data.setArmor(armor);
        
        //エフェクト
        BukkitRunnable effect_r = new BukkitRunnable(){
            @Override
            public void run(){
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter() && !o_player.equals(player))
                        o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, player.getEyeLocation(), 2, 0.5, 1, 0.5, 2, bd);
                }
            }
        };
        if(effect)
            effect_r.runTaskTimer(Main.getPlugin(), 0, 1);
        
        BukkitRunnable task = new BukkitRunnable(){
            @Override
            public void run(){
                data.setArmor(0);
                effect_r.cancel();
            }
        };
        task.runTaskLater(Main.getPlugin(), delay);
        
    }  
}
