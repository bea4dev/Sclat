
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.weapon.Gear;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Be4rJP
 */
public class SplashBomb {
    
    public static void SplashBomRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            int c = 0;
            int gc = 0;
            Item drop;
            @Override
            public void run(){
                try{
                    if(c == 0){
                        if(!DataMgr.getPlayerData(player).getIsBombRush())
                            p.setExp(p.getExp() - 0.59F);
                        ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getGlass()).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                        bom.setItemMeta(bom_m);
                        drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                        drop.setVelocity(p.getEyeLocation().getDirection());
                    }

                    if(gc >= 10 && gc < 20){
                        if(gc % 2 == 0)
                            player.getWorld().playSound(drop.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1.6F);
                    }

                    if(gc == 30){
                        //爆発音
                        player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);

                        //爆発エフェクト
                        Sclat.createInkExplosionEffect(drop.getLocation(), 5, 15, player);

                        double maxDist = 4;
    
                        //バリアをはじく
                        Sclat.repelBarrier(drop.getLocation(), maxDist, player);
                        
                        //塗る
                        for(int i = 0; i <= maxDist; i++){
                            List<Location> p_locs = Sphere.getSphere(drop.getLocation(), i, 14);
                            for(Location loc : p_locs){
                                PaintMgr.Paint(loc, p, false);
                            }
                        }



                        //攻撃判定の処理

                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != p.getWorld())
                                continue;
                            if (target.getLocation().distance(drop.getLocation()) <= maxDist) {
                                double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 14 * Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP);
                                if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    Sclat.giveDamage(player, target, damage, "subWeapon");

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
                            if (as.getLocation().distance(drop.getLocation()) <= maxDist){
                                if(as instanceof ArmorStand){
                                    if(as.getCustomName() != null) {
                                        double damage = (maxDist - as.getLocation().distance(drop.getLocation())) * 12;
                                        ArmorStandMgr.giveDamageArmorStand((ArmorStand) as, damage, p);
                                    }
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
                            if(o_player.getWorld() == drop.getLocation().getWorld()) {
                                if (o_player.getLocation().distanceSquared(drop.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED) {
                                    Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                                    o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
                                }
                            }
                        }
                    }

                    c++;

                    if(c > 500){
                        drop.remove();
                        cancel();
                        return;
                    }

                    if(drop.isOnGround())
                        gc++;
                }catch(Exception e){
                    drop.remove();
                    cancel();
                    Main.getPlugin().getLogger().warning(e.getMessage());
                }
            }
        };
        
        BukkitRunnable cooltime = new BukkitRunnable(){
            @Override
            public void run(){
                DataMgr.getPlayerData(player).setCanUseSubWeapon(true);
            }
        };
        cooltime.runTaskLater(Main.getPlugin(), 10);
        
        if(player.getExp() > 0.6 || DataMgr.getPlayerData(player).getIsBombRush())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else{
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
        }
    }
}
