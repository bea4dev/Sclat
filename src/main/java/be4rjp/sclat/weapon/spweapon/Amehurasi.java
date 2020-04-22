
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import java.util.List;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Amehurasi {
    public static void AmehurasiDropRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Vector p_vec;
            double x = 0;
            double z = 0;
            boolean collision = false;
            boolean block_check = false;
            boolean cb = false;
            Location l = p.getLocation();
            int cc = 0;
            int c = 0;
            Item drop;
            @Override
            public void run(){
                if(c == 0){
                    ItemStack bom = new ItemStack(Material.BEACON).clone();
                    ItemMeta bom_m = bom.getItemMeta();
                    bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                    bom.setItemMeta(bom_m);
                    drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                    drop.setVelocity(p.getEyeLocation().getDirection());
                    p_vec = p.getEyeLocation().getDirection();
                }
                
                
                if(drop.isOnGround()){
                    AmehurasiRunnable(p, drop.getLocation());
                    drop.remove();
                    cancel();
                }
                
                //視認用エフェクト
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                        Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                        o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                    }
                }
                
                c++;
                
                if(c > 1000){
                    drop.remove();
                    cancel();
                    return;
                }
                
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void AmehurasiRunnable(Player player, Location loc){
        DataMgr.getPlayerData(player).setIsUsingSP(true);
        
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            List<Location> locList = Sphere.getXZCircle(loc.add(0, 18, 0), 10, 100);
            @Override
            public void run(){
                
                //雲エフェクト
                if(c % 2 == 0){
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                            for(Location loc : locList){
                                Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                o_player.spawnParticle(Particle.REDSTONE, loc, 1, 1, 1, 1, 50, dustOptions);
                            }
                        }
                    }
                }
                for(Location loc : locList){
                    if(new Random().nextInt(400) == 1)
                        SnowballAmehurasiRunnable(p, loc);
                }
                if(c == 220 || !DataMgr.getPlayerData(p).isInMatch()){
                    DataMgr.getPlayerData(player).setIsUsingSP(false);
                    cancel();
                }
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void SnowballAmehurasiRunnable(Player player, Location loc){
        Snowball ball = (Snowball)player.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
        ball.setShooter(player);
        ball.setCustomName("Amehurasi");
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            Snowball inkball = ball;
            Player p = player;
            @Override
            public void run(){
                if(i % 2 == 0){
                    org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Shooter())
                            o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                    }
                }

                if(inkball.isDead())
                    cancel();

                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 2);
    }
}
