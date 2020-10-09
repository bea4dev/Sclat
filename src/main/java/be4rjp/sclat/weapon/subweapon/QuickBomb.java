
package be4rjp.sclat.weapon.subweapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.DamageMgr;
import be4rjp.sclat.manager.DeathMgr;
import be4rjp.sclat.manager.PaintMgr;
import be4rjp.sclat.weapon.Gear;
import java.util.List;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
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
public class QuickBomb {
    
    public static void QuickBomRunnable(Player player){
        BukkitRunnable task = new BukkitRunnable(){
            Player p = player;
            Vector p_vec;
            double x = 0;
            double z = 0;
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
                        ItemStack bom = new ItemStack(DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool()).clone();
                        ItemMeta bom_m = bom.getItemMeta();
                        bom_m.setLocalizedName(String.valueOf(Main.getNotDuplicateNumber()));
                        bom.setItemMeta(bom_m);
                        drop = p.getWorld().dropItem(p.getEyeLocation(), bom);
                        drop.setVelocity(p_vec.clone().multiply(0.9));
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
                        double maxDist = 3;

                        //爆発音
                        player.getWorld().playSound(drop.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);

                        //爆発エフェクト
                        List<Location> s_locs = Sphere.getSphere(drop.getLocation(), maxDist, 25);
                        for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_BombEx()){
                                for(Location loc : s_locs){
                                    if(o_player.getWorld() == loc.getWorld()){
                                        if(o_player.getLocation().distance(loc) < conf.getConfig().getInt("ParticlesRenderDistance")){
                                            org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool().createBlockData();
                                            o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, loc, 1, 0, 0, 0, 1, bd);
                                        }
                                    }
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
                        
                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(drop.getLocation()) <= maxDist){
                                if(as instanceof ArmorStand){
                                    if(as.getCustomName() != null){
                                        if(as.getCustomName().equals("Kasa")){
                                            drop.remove();
                                            cancel();
                                            return;
                                        }
                                    }
                                            
                                }
                            }
                        }

                        for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                            if(!DataMgr.getPlayerData(target).isInMatch() || target.getWorld() != p.getWorld())
                                continue;
                            if (target.getLocation().distance(drop.getLocation()) <= maxDist) {
                                double damage = (maxDist - target.getLocation().distance(drop.getLocation())) * 5 * Gear.getGearInfluence(player, Gear.Type.SUB_SPEC_UP);
                                if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                    if(target.getHealth() + DataMgr.getPlayerData(target).getArmor() > damage){
                                        DamageMgr.SclatGiveDamage(target, damage);
                                        PaintMgr.Paint(target.getLocation(), player, true);
                                    }else{
                                        target.setGameMode(GameMode.SPECTATOR);
                                        DeathMgr.PlayerDeathRunnable(target, player, "subWeapon");
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

                        for(Entity as : player.getWorld().getEntities()){
                            if (as.getLocation().distance(drop.getLocation()) <= maxDist){
                                if(as instanceof ArmorStand){
                                    double damage = (maxDist - as.getLocation().distance(drop.getLocation())) * 5 * Gear.getGearInfluence(p, Gear.Type.SUB_SPEC_UP);
                                    ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                                    if(as.getCustomName() != null){
                                        if(as.getCustomName().equals("SplashShield") || as.getCustomName().equals("Kasa"))
                                            break;
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
                            Particle.DustOptions dustOptions = new Particle.DustOptions(DataMgr.getPlayerData(p).getTeam().getTeamColor().getBukkitColor(), 1);
                            o_player.spawnParticle(Particle.REDSTONE, drop.getLocation(), 1, 0, 0, 0, 50, dustOptions);
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
                
        if(player.getExp() > 0.4 || DataMgr.getPlayerData(player).getIsBombRush())
            task.runTaskTimer(Main.getPlugin(), 0, 1);
        else
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
    }
}
