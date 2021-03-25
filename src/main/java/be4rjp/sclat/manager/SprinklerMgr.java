
package be4rjp.sclat.manager;

import be4rjp.sclat.Main;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class SprinklerMgr {
    public static void sprinklerShoot(Player player, ArmorStand as, Vector vec){
        PlayerData data = DataMgr.getPlayerData(player);
        Snowball ball = (Snowball)player.getWorld().spawnEntity(as.getLocation().add(0, 0.5, 0), EntityType.SNOWBALL);
        ((CraftSnowball)ball).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool())));
        player.getWorld().playSound(as.getLocation(), Sound.ENTITY_PIG_STEP, 0.1F, 1F);
        double random = 1.2;
        vec.add(new Vector(Math.random() * random - random/2, Math.random() * random - random/2, Math.random() * random - random/2));
        ball.setVelocity(vec);
        ball.setShooter(player);
        ball.setCustomName("Sprinkler");
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            int tick = 2;
            //Vector fallvec;
            Vector origvec = vec;
            Snowball inkball = ball;
            Player p = player;
            @Override
            public void run(){
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_MainWeaponInk())
                        o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                }
                if(i >= tick)
                    inkball.setVelocity(inkball.getVelocity().add(new Vector(0, -0.1, 0)));
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
