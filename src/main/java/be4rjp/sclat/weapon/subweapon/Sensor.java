
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.weapon.Gear;
import java.util.List;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Sensor {
    public static void SensorRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Vector p_vec;
            double x = 0;
            double z = 0;
            boolean collision = false;
            boolean block_check = false;
            int c = 0;
            Item drop;
            Snowball ball;
            @Override
            public void run(){
                try{
                    if(c == 0){
                        p_vec = p.getEyeLocation().getDirection();
                        if(!DataMgr.getPlayerData(player).getIsBombRush())
                            p.setExp(p.getExp() - 0.39F);
                        ItemStack bom = new ItemStack(Material.DISPENSER).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                        bom.setItemMeta(bom_m);
                        drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                        drop.setVelocity(p_vec);
                        //雪玉をスポーンさせた瞬間にプレイヤーに雪玉がデスポーンした偽のパケットを送信する
                        ball = player.launchProjectile(Snowball.class);
                        ball.setVelocity(new Vector(0, 0, 0));
                        DataMgr.setSnowballIsHit(ball, false);

                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            PlayerConnection connection = ((CraftPlayer) o_player).getHandle().playerConnection;
                            connection.sendPacket(new PacketPlayOutEntityDestroy(ball.getEntityId()));
                        }
                        p_vec = p.getEyeLocation().getDirection();
                    }

                    if(!drop.isOnGround() && !(drop.getVelocity().getX() == 0 && drop.getVelocity().getZ() != 0) && !(drop.getVelocity().getX() != 0 && drop.getVelocity().getZ() == 0))
                        ball.setVelocity(drop.getVelocity());             

                    if(DataMgr.getSnowballIsHit(ball) || drop.isOnGround()){

                        //半径
                        double maxDist = (int)5 * Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP);

                        //爆発音
                        player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 2);

                        //爆発エフェクト
                        List<Location> s_locs = Sphere.getSphere(drop.getLocation(), maxDist, 15);
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                                for(Location loc : s_locs){
                                    if(o_player.getWorld() == loc.getWorld()){
                                        if(o_player.getLocation().distanceSquared(loc) < Main.PARTICLE_RENDER_DISTANCE_SQUARED){
                                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.BLACK, 1);
                                            o_player.spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 1, dustOptions);
                                        }
                                    }
                                }
                            }
                        }





                        //あたり判定の処理

                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != p.getWorld())
                                continue;
                            if (target.getLocation().distance(drop.getLocation()) <= maxDist) {
                                if(DataMgr.getPlayerData(player).getTeam().getID() != DataMgr.getPlayerData(target).getTeam().getID()){
                                    target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
                                }

                            }
                        }

                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(drop.getLocation()) <= maxDist){
                                if(as.getCustomName() != null){
                                    if(as.getCustomName() == null) continue;
                                    if(as instanceof ArmorStand && !as.getCustomName().equals("Path") && !as.getCustomName().equals("21") && !as.getCustomName().equals("100")&& !as.getCustomName().equals("SplashShield") && !as.getCustomName().equals("Kasa")){
                                        ((ArmorStand)as).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 200, 1));
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
                    x = drop.getLocation().getX();
                    z = drop.getLocation().getZ();


                    if(c > 1000){
                        drop.remove();
                        cancel();
                        return;
                    }
                }catch(Exception e){
                    cancel();
                    drop.remove();
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
        cooltime.runTaskLater(Main.getPlugin(), 8);
                
        if(player.getExp() > 0.3 || DataMgr.getPlayerData(player).getIsBombRush())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else{
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
        }

    }
}
