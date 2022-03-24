
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
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class KBomb {
    public static void KBomRunnable(Player player){
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
            Snowball ball;
            int ndn;
            @Override
            public void run(){
                try{
                    if(c == 0){
                        if(!DataMgr.getPlayerData(player).getIsBombRush())
                            p.setExp(p.getExp() - 0.59F);
                        ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getConcrete()).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        ndn = Main.getNotDuplicateNumber();
                        bom_m.setLocalizedName(String.valueOf(ndn));
                        bom.setItemMeta(bom_m);
                        drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                        drop.setVelocity(p.getEyeLocation().getDirection());
                        //雪玉をスポーンさせた瞬間にプレイヤーに雪玉がデスポーンした偽のパケットを送信する
                        ball = player.launchProjectile(Snowball.class);
                        ball.setVelocity(new Vector(0, 0, 0)); 
                        ball.setCustomName(String.valueOf(ndn));
                        DataMgr.setSnowballIsHit(ball, false);
                        DataMgr.getSnowballNameMap().put(String.valueOf(ndn), ball);

                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            PlayerConnection connection = ((CraftPlayer) o_player).getHandle().playerConnection;
                            connection.sendPacket(new PacketPlayOutEntityDestroy(ball.getEntityId()));
                        }
                        p_vec = p.getEyeLocation().getDirection();
                    }
                    
                    ball = DataMgr.getSnowballNameMap().get(String.valueOf(ndn));
                    
                    if(!drop.isOnGround() && !(drop.getVelocity().getX() == 0 && drop.getVelocity().getZ() != 0) && !(drop.getVelocity().getX() != 0 && drop.getVelocity().getZ() == 0))
                        ball.setVelocity(drop.getVelocity());

                    if(DataMgr.getSnowballIsHit(ball) || drop.isOnGround())
                        cb = true;

                    if(!cb)
                        l = drop.getLocation();

                    if(cb){
                        drop.setGravity(false);
                        drop.setVelocity(new Vector(0, 0, 0));
                        cc++;
                    }
                    
                    if(cc >= 40 && cc < 50){
                        if(cc % 2 == 0)
                            player.getWorld().playSound(drop.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1.6F);
                    }

                    if(cc == 60){

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
                                double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 17 * Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP);
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
                                    double damage = (maxDist - as.getLocation().distance(drop.getLocation())) * 12;
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
        cooltime.runTaskLater(Main.getPlugin(), 10);
        
        
        
                
        if(player.getExp() > 0.6 || DataMgr.getPlayerData(player).getIsBombRush())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else{
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
        }
    }
    
    
    
}
