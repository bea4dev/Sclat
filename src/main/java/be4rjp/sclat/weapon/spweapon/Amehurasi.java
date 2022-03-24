
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.WeaponClassMgr;
import be4rjp.sclat.raytrace.RayTrace;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
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
            Vector vec;
            
            @Override
            public void run(){
                try{
                    if(c == 0){
                        DataMgr.getPlayerData(player).setIsUsingAmehurashi(true);
                        ItemStack bom = new ItemStack(Material.BEACON).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                        bom.setItemMeta(bom_m);
                        drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                        drop.setVelocity(p.getEyeLocation().getDirection());
                        p_vec = p.getEyeLocation().getDirection();
                        vec = new Vector(p_vec.getX(), 0, p_vec.getZ()).normalize();
                    }


                    if(drop.isOnGround()){
                        AmehurasiRunnable(p, drop.getLocation(), vec);
                        drop.remove();
                        cancel();
                    }
                    
                    if(drop.getLocation().getY() <= 0 || drop.isDead()){
                        DataMgr.getPlayerData(player).setIsUsingAmehurashi(false);
                        WeaponClassMgr.setWeaponClass(p);
                        cancel();
                    }

                    //視認用エフェクト
                    for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_Bomb()){
                            if(o_player.getWorld() == drop.getWorld()){
                                if(o_player.getLocation().distanceSquared(drop.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                    o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                                }
                            }
                        }
                    }

                    c++;

                    if(c > 1000){
                        drop.remove();
                        cancel();
                        return;
                    }
                }catch(Exception e){
                    drop.remove();
                    cancel();
                    Main.getPlugin().getLogger().warning(e.getMessage());
                }
            }
        };
        if(!DataMgr.getPlayerData(player).getIsUsingAmehurashi())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void AmehurasiRunnable(Player player, Location loc, Vector vec){
        DataMgr.getPlayerData(player).setIsUsingSP(true);
        SPWeaponMgr.setSPCoolTimeAnimation(player, 260);
        
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            List<Location> locList = Sphere.getXZCircle(loc.clone().add(0, 18, 0), 8, 1, 100);
            @Override
            public void run(){
                try{
                    if(c % 4 == 0){
                        locList.clear();
                        locList = Sphere.getXZCircle(loc.clone().add(vec.getX() * c / 12, 18, vec.getZ() * c / 12), 8, 1, 100);
                    }

                    //雲エフェクト
                    if(c % 2 == 0){
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon()){
                                for(Location loc : locList){
                                    if(new Random().nextInt(3) == 1){
                                        if(o_player.getWorld() == loc.getWorld()){
                                            if(o_player.getLocation().distanceSquared(loc) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                                Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 3);
                                                o_player.spawnParticle(Particle.REDSTONE, loc, 1, 1, 1, 1, 1, dustOptions);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    
                    if(c >= 30){
                        //攻撃判定
                        RayTrace rayTrace4 = new RayTrace(loc.clone().add(vec.getX() * c / 12, 18, vec.getZ() * c / 12).toVector(), new Vector(0, -1, 0));
                        ArrayList<Vector> positions4 = rayTrace4.traverse(300, 1);
                        for(int i = 1; i < positions4.size();i++){
                            Location position = positions4.get(i).toLocation(p.getLocation().getWorld());

                            if(position.getBlock().getType() != Material.AIR) break;

                            double maxDist = 6.5;
                            double maxDistSquared = 42.25; /* 6.5^2 */
                            double damage = 2;
                            for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                                if(!DataMgr.getPlayerData(target).isInMatch())
                                    continue;
                                if(target.getWorld() != p.getWorld())
                                    continue;
                                if (target.getLocation().distanceSquared(position) <= maxDistSquared && new Random().nextInt(100) == 0) {
                                    if(DataMgr.getPlayerData(p).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                        Sclat.giveDamage(p, target, damage, "spWeapon");

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
                                if (as instanceof ArmorStand && as.getLocation().distanceSquared(position) <= maxDistSquared && new Random().nextInt(100) == 0){
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, player);
                                }
                            }
                        }
                    }
                    
                    for(Location loc : locList){
                        if(new Random().nextInt(200) == 1)
                            SnowballAmehurasiRunnable(p, loc);
                    }
                    if(c == 260 || !DataMgr.getPlayerData(p).isInMatch()){
                        DataMgr.getPlayerData(player).setIsUsingSP(false);
                        //p.playSound(p.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1, 2);
                        cancel();
                    }
                    c++;
                }catch(Exception e){
                    cancel();
                    Main.getPlugin().getLogger().warning(e.getMessage());
                }
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
    public static void SnowballAmehurasiRunnable(Player player, Location loc){
        Snowball ball = (Snowball)player.getWorld().spawnEntity(loc, EntityType.SNOWBALL);
        ((CraftSnowball)ball).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool())));
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
                        if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon())
                            if(o_player.getWorld() == inkball.getWorld())
                                if(o_player.getLocation().distanceSquared(inkball.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
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
