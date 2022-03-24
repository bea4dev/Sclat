
package be4rjp.sclat.weapon.spweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.manager.SPWeaponMgr;
import be4rjp.sclat.manager.WeaponClassMgr;

import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class SuperTyakuti {
    public static void SuperTyakutiRunnable(Player player){
        player.getInventory().clear();
        DataMgr.getPlayerData(player).setIsUsingSP(true);
        DataMgr.getPlayerData(player).setIsUsingTyakuti(true);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.3F, 0.5F);
        SPWeaponMgr.setSPCoolTimeAnimation(player, 40);
        BukkitRunnable task = new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                try{
                    player.getInventory().clear();
                    Vector vec = new Vector(0, 0, 0);
                    switch (i) {
                        case 1:
                            vec = new Vector(0, 3, 0);
                            break;
                        case 2:
                            vec = new Vector(0, 2.5, 0);
                            break;
                        case 3:
                            vec = new Vector(0, 2, 0);
                            break;
                        case 4:
                            vec = new Vector(0, 1, 0);
                            break;
                        case 24:
                            vec = new Vector(0, -0.5, 0);
                            break;
                        case 25:
                            vec = new Vector(0, -1, 0);
                            break;
                        case 26:
                            vec = new Vector(0, -2, 0);
                            break;
                        case 27:
                            vec = new Vector(0, -4, 0);
                            break;
                        default:
                            break;
                    }
                    if(i <= 27)
                        player.setVelocity(vec);
                    
                    if(i >= 5 && i <= 23){
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeapon() && !o_player.equals(player)){
                                if(o_player.getWorld() == player.getWorld()){
                                    if(o_player.getLocation().distanceSquared(player.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                        Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(player).getTeam().getTeamColor().getBukkitColor(), 1);
                                        o_player.spawnParticle(Particle.REDSTONE, player.getEyeLocation().add(0, -0.5, 0), 5, 0.5, 0.4, 0.5, 5, dustOptions);
                                    }
                                }
                            }
                        }
                    }
                    
                    if(i == 15)
                        SuperArmor.setArmor(player, 20, 20, false);

                    //範囲エフェクト
                    if(i % 5 == 0){
                        Location bloc = player.getWorld().getHighestBlockAt(player.getLocation().getBlockX(), player.getLocation().getBlockZ()).getLocation();
                        for(int y = player.getLocation().getBlockY(); y > 0; y--){
                            Location bl = new Location(player.getWorld(), player.getLocation().getX(), y, player.getLocation().getZ());
                            if(!bl.getBlock().getType().equals(Material.AIR)){
                                bloc = bl;
                                break;
                            }
                        }
                        List<Location> s_locs = Sphere.getXZCircle(bloc.add(0, 1, 0), 7, 3, 40);
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_SPWeaponRegion()){
                                for(Location loc : s_locs){
                                    if(o_player.getWorld() == loc.getWorld()){
                                        if(o_player.getLocation().distanceSquared(loc) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                                            o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, loc, 1, 0, 0, 0, 1, bd);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(i >= 24 && player.isOnGround()){
                        //爆発音
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.2F, 0.8F);
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, 1.1F, 0.9F);

                        //爆発エフェクト
                        Sclat.createInkExplosionEffect(player.getLocation(), 7, 10, player);

                        double maxDist = 8;
                        double maxDistSquared = 64; /* 8^2 */
                        //塗る
                        for(int i = 0; i <= maxDist; i++){
                            List<Location> p_locs = Sphere.getSphere(player.getLocation(), i, 10);
                            for(Location loc : p_locs){
                                PaintMgr.Paint(loc, player, false);
                            }
                        }

                        //攻撃判定の処理

                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != player.getWorld())
                                continue;
                            if (target.getLocation().distanceSquared(player.getLocation()) <= maxDistSquared) {
                                double damage = (maxDist - target.getLocation().distance(player.getLocation())) * 15;
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
                            if (as.getLocation().distanceSquared(player.getLocation()) <= maxDistSquared){
                                if(as instanceof ArmorStand){
                                    if(as.getCustomName() != null) {
                                        double damage = (maxDist - as.getLocation().distance(player.getLocation())) * 15;
                                        ArmorStandMgr.giveDamageArmorStand((ArmorStand) as, damage, player);
                                    }
                                }        
                            }
                        }
                        WeaponClassMgr.setWeaponClass(player);
                        DataMgr.getPlayerData(player).setIsUsingSP(false);
                        DataMgr.getPlayerData(player).setIsUsingTyakuti(false);
                        cancel();
                    }


                    if(i == 200 || player.getGameMode().equals(GameMode.SPECTATOR) || !DataMgr.getPlayerData(player).isInMatch()){
                        DataMgr.getPlayerData(player).setIsUsingSP(false);
                        DataMgr.getPlayerData(player).setIsUsingTyakuti(false);
                        cancel();
                    }
                    i++;
                }catch(Exception e){cancel();}
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}
