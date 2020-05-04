
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.manager.SPWeaponMgr;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class Barrier {
    public static void BarrierRunnable(Player player){
        DataMgr.getPlayerData(player).setIsUsingSP(true);
        PlayerData data = DataMgr.getPlayerData(player);
        data.setArmor(Double.MAX_VALUE);
        SPWeaponMgr.setSPCoolTimeAnimation(player, 120);
        
        //エフェクトとアーマー解除
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            @Override
            public void run(){
                if(!data.isInMatch() || !player.getGameMode().equals(GameMode.ADVENTURE) || !p.isOnline())
                    cancel();
                Location loc = p.getLocation().add(0, 0.5, 0);
                List<Location> s_locs = Sphere.getSphere(loc, 2, 25);
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx() && !o_player.equals(player)){
                        Particle.DustOptions dustOptions = new Particle.DustOptions(data.getTeam().getTeamColor().getBukkitColor(), 1);
                        for(Location e_loc : s_locs)
                            o_player.spawnParticle(Particle.REDSTONE, e_loc, 1, 0, 0, 0, 70, dustOptions);
                    }
                }
                if(c == 30){
                    data.setArmor(0);
                    p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 2);
                    DataMgr.getPlayerData(player).setIsUsingSP(false);
                    cancel();
                }
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 4);
    }
}
