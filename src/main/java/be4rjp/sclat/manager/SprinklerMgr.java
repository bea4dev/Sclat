
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class SprinklerMgr {
    public static void sprinklerShoot(Player player, ArmorStand as, Vector vec){
        PlayerData data = DataMgr.getPlayerData(player);
        Snowball ball = (Snowball)player.getWorld().spawnEntity(as.getLocation(), EntityType.SNOWBALL);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIG_STEP, 0.2F, 1F);
        double random = 0.4;
        int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
        vec.add(new Vector(Math.random() * random - random/2, Math.random() * random - random/2, Math.random() * random - random/2));
        ball.setVelocity(vec);
        ball.setShooter(player);
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            int tick = distick;
            //Vector fallvec;
            Vector origvec = vec;
            Snowball inkball = ball;
            Player p = player;
            @Override
            public void run(){
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter())
                        o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                }
                if(i != tick)
                    PaintMgr.PaintHightestBlock(inkball.getLocation(), p, true, true);
                if(inkball.isDead())
                    cancel();

                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
