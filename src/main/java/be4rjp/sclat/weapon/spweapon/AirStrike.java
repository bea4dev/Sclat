
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.MapKitMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class AirStrike {
    public static void AirStrikeRunnable(Player player){
        Firework f = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
        if(player.hasPotionEffect(PotionEffectType.SLOW))
            player.removePotionEffect(PotionEffectType.SLOW);
        player.getInventory().clear();
        WeaponClassMgr.setWeaponClass(player);
        Vector vec = MapKitMgr.getMapLocationVector(player);
        //int y = player.getWorld().getHighestBlockYAt(vec.getBlockX(), vec.getBlockZ());
        int y = player.getWorld().getHighestBlockYAt(vec.getBlockX(), vec.getBlockZ());
        Location tloc = new Location(player.getWorld(), player.getLocation().getBlockX() + vec.getBlockX(), y, player.getLocation().getBlockZ() + vec.getBlockZ());
        BukkitRunnable task = new BukkitRunnable(){
            int c = 0;
            @Override
            public void run(){
                double random = 17;
                Location loc = new Location(player.getWorld(), player.getLocation().getBlockX() + vec.getBlockX() + (Math.random() * random - random/2), y + 50, player.getLocation().getBlockZ() + vec.getBlockZ() + (Math.random() * random - random/2));
                StrikeRunnable(player, loc);
                if(c == 15)
                    cancel();
                c++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 50, 10);
        
        BukkitRunnable effect = new BukkitRunnable(){
            int c = 0;
            @Override
            public void run(){
                RayTrace rayTrace = new RayTrace(tloc.toVector(),new Vector(0, 1, 0));
                ArrayList<Vector> positions = rayTrace.traverse(50, 0.8);
                check : for(int i = 0; i < positions.size();i++){
                    Location position = positions.get(i).toLocation(player.getLocation().getWorld());
                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(player).getTeam().getTeamColor().getBukkitColor(), 1);
                    player.getWorld().spawnParticle(Particle.REDSTONE, position, 1, 0, 0, 0, 1, dustOptions);
                }
                if(c == 150)
                    cancel();
                c++;
            }
        };
        effect.runTaskTimer(Main.getPlugin(), 0, 2);
    }
    
    public static void StrikeRunnable(Player player, Location loc){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Vector p_vec;
            double x = 0;
            double z = 0;
            boolean collision = false;
            boolean block_check = false;
            int c = 0;
            Item drop;
            @Override
            public void run(){
                if(c == 0){
                    ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool()).clone();
                    ItemMeta bom_m = bom.getItemMeta();
                    //bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                    bom.setItemMeta(bom_m);
                    drop = p.getWorld().dropItem(loc, bom);
                    drop.setVelocity(new Vector(0, -1, 0));
                }
                
                if(drop.isOnGround()){
                    
                    //半径
                    double maxDist = 4;
                    
                    //爆発音
                    player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    
                    //爆発エフェクト
                    List<Location> s_locs = Sphere.getSphere(drop.getLocation(), maxDist, 20);
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                            for(Location loc : s_locs){
                                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                                o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, loc, 1, 0, 0, 0, 1, bd);
                            }
                        }
                    }
                    
                    //塗る
                    for(int i = 0; i <= maxDist; i++){
                        List<Location> p_locs = Sphere.getSphere(drop.getLocation(), i, 20);
                        for(Location loc : p_locs){
                            PaintMgr.Paint(loc, p, false);
                        }
                    }
                    
                    
                    
                    //攻撃判定の処理
               
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(target).isInMatch())
                            continue;
                        if (target.getLocation().distance(drop.getLocation()) <= maxDist) {
                            double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 7;
                            if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                if(target.getHealth() > damage){
                                    DamageMgr.SclatGiveDamage(target, damage);
                                    PaintMgr.Paint(target.getLocation(), player, true);
                                }else{
                                    target.setGameMode(GameMode.SPECTATOR);
                                    DeathMgr.PlayerDeathRunnable(target, player, "spWeapon");
                                    PaintMgr.Paint(target.getLocation(), player, true);
                                }

                                //AntiNoDamageTime
                                BukkitRunnable task = new BukkitRunnable(){
                                    Player p = target;
                                    @Override
                                    public void run(){
                                        target.setNoDamageTicks(0);
                                    }
                                };
                                task.runTaskLater(Main.getPlugin(), 1);
                                
                                
                            }
                        }
                    }
                    drop.remove();
                    cancel();
                    return;
                }
                
                //ボムの視認用エフェクト
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                        Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                        o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                    }
                }
                
                c++;
                x = drop.getLocation().getX();
                z = drop.getLocation().getZ();

                
                if(c > 2000){
                    drop.remove();
                    cancel();
                    return;
                }
                
            }
        };
        

        task.runTaskTimer(Main.getPlugin(), 0, 1);

    }
}
