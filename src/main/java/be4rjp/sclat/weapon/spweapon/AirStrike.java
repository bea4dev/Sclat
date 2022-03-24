
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.MapKitMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
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
        player.getInventory().clear();
        SPWeaponMgr.setSPCoolTimeAnimation(player, 200);
        
        BukkitRunnable clear = new BukkitRunnable(){
            @Override
            public void run(){
                WeaponClassMgr.setWeaponClass(player);
                if(player.hasPotionEffect(PotionEffectType.SLOW))
                    player.removePotionEffect(PotionEffectType.SLOW);
            }
        };
        clear.runTaskLater(Main.getPlugin(), 20);
        
        Vector vec = MapKitMgr.getMapLocationVector(player);
        //int y = player.getWorld().getHighestBlockYAt(vec.getBlockX(), vec.getBlockZ());
        
        int c = 0;
        for (int i = 254; i > 0; i--){
            Location locc = new Location(player.getWorld(), player.getLocation().getBlockX() + vec.getBlockX(), i, player.getLocation().getBlockZ() + vec.getBlockZ());
            Block block = player.getWorld().getBlockAt(locc);
            if(!block.getType().equals(Material.AIR)) {
                c = i;
                break;
            }
        }
        int y = c;
        Location ploc = player.getLocation();
        Location tloc = new Location(player.getWorld(), player.getLocation().getBlockX() + vec.getBlockX(), y, player.getLocation().getBlockZ() + vec.getBlockZ());
        BukkitRunnable task = new BukkitRunnable(){
            int c = 0;
            @Override
            public void run(){
                if(c == 0) DataMgr.getPlayerData(player).setIsUsingSP(true);
                final double random = 18;
                Location loc = new Location(ploc.getWorld(), ploc.getBlockX() + vec.getBlockX() + (Math.random() * random - random/2), y + 50, ploc.getBlockZ() + vec.getBlockZ() + (Math.random() * random - random/2));
                StrikeRunnable(player, loc);
                if(c == 15 || !DataMgr.getPlayerData(player).isInMatch()){
                    //player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 2);
                    cancel();
                }
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
                if(c == 100 || !DataMgr.getPlayerData(player).isInMatch()){
                    DataMgr.getPlayerData(player).setIsUsingSP(false);
                    cancel();
                }
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
                    bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                    bom.setItemMeta(bom_m);
                    drop = p.getWorld().dropItem(loc, bom);
                    drop.setVelocity(new Vector(0, -1, 0));
                }
                
                if(drop.isOnGround()){
                    
                    //半径
                    double maxDist = 4;
                    double maxDistSquared = 16; /* 4^2 */
                    
                    //爆発音
                    player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    
                    //爆発エフェクト
                    Sclat.createInkExplosionEffect(drop.getLocation(), maxDist, 25, player);
                    
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
                        if (target.getLocation().distanceSquared(drop.getLocation()) <= maxDistSquared) {
                            double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 7;
                            if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                Sclat.giveDamage(player, target, damage, "spWeapon");

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
                    
                    for(Entity as : player.getWorld().getEntities()){
                        if (as.getLocation().distanceSquared(drop.getLocation()) <= maxDistSquared){
                            if(as instanceof ArmorStand){
                                double damage = (maxDist - as.getLocation().distance(drop.getLocation())) * 7;
                                ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                            }         
                        }
                    }
                    
                    drop.remove();
                    cancel();
                    return;
                }
                
                //ボムの視認用エフェクト
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon()){
                        Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                        o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                    }
                }
                
                c++;
                x = drop.getLocation().getX();
                z = drop.getLocation().getZ();

                
                if(c > 2000 || !DataMgr.getPlayerData(p).isInMatch()){
                    drop.remove();
                    cancel();
                    return;
                }
                
            }
        };
        

        task.runTaskTimer(Main.getPlugin(), 0, 1);

    }
}
