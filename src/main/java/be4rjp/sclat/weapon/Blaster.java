
package be4rjp.sclat.weapon;

import be4rjp.sclat.Main;
import static be4rjp.sclat.Main.conf;

import be4rjp.sclat.Sclat;
import be4rjp.sclat.Sphere;
import be4rjp.sclat.data.DataMgr;
import be4rjp.sclat.data.KasaData;
import be4rjp.sclat.data.PlayerData;
import be4rjp.sclat.data.SplashShieldData;
import be4rjp.sclat.manager.ArmorStandMgr;
import be4rjp.sclat.manager.PaintMgr;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftSnowball;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Be4rJP
 */
public class Blaster {
    public static void ShootBlaster(Player player){
        PlayerData data = DataMgr.getPlayerData(player);
        BukkitRunnable delay1 = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                PlayerData data = DataMgr.getPlayerData(player);
                data.setCanRollerShoot(true);
            }
        };
        if(data.getCanRollerShoot())
            delay1.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getCoolTime());
        
        BukkitRunnable delay = new BukkitRunnable(){
            Player p = player;
            @Override
            public void run(){
                Shoot(player);
            }
        };
        if(data.getCanRollerShoot()){
            delay.runTaskLater(Main.getPlugin(), data.getWeaponClass().getMainWeapon().getDelay());
            data.setCanRollerShoot(false);
        }
    }
    
    public static void Shoot(Player player){
    
        if(player.getGameMode() == GameMode.SPECTATOR) return;
        
        PlayerData data = DataMgr.getPlayerData(player);
        data.setCanRollerShoot(false);
        if(player.getExp() <= (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP))){
            player.sendTitle("", ChatColor.RED + "インクが足りません", 0, 5, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1.63F);
            return;
        }
        player.setExp(player.getExp() - (float)(data.getWeaponClass().getMainWeapon().getNeedInk() / Gear.getGearInfluence(player, Gear.Type.MAIN_INK_EFFICIENCY_UP)));
        Snowball ball = player.launchProjectile(Snowball.class);
        ((CraftSnowball)ball).getHandle().setItem(CraftItemStack.asNMSCopy(new ItemStack(DataMgr.getPlayerData(player).getTeam().getTeamColor().getWool())));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PIG_STEP, 0.3F, 1F);
        Vector vec = player.getLocation().getDirection().multiply(DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getShootSpeed());
        double random = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getRandom();
        int distick = DataMgr.getPlayerData(player).getWeaponClass().getMainWeapon().getDistanceTick();
        if(!player.isOnGround())
            vec.add(new Vector(Math.random() * random - random/2, 0, Math.random() * random - random/2));
        ball.setVelocity(vec);
        ball.setShooter(player);
        ball.setGravity(false);
        String name = String.valueOf(Main.getNotDuplicateNumber());
        DataMgr.mws.add(name);
        DataMgr.tsl.add(name);
        ball.setCustomName(name);
        DataMgr.getMainSnowballNameMap().put(name, ball);
        DataMgr.setSnowballHitCount(name, 0);
        BukkitRunnable task = new BukkitRunnable(){
            int i = 0;
            int tick = distick;
            //Vector fallvec;
            Vector origvec = vec;
            Snowball inkball = ball;
            Player p = player;
            Vector fallvec = new Vector(inkball.getVelocity().getX(), inkball.getVelocity().getY()  , inkball.getVelocity().getZ()).multiply(DataMgr.getPlayerData(p).getWeaponClass().getMainWeapon().getShootSpeed()/17);
            @Override
            public void run(){
                inkball = DataMgr.getMainSnowballNameMap().get(name);
                        
                    if(!inkball.equals(ball)){
                        i+=DataMgr.getSnowballHitCount(name) - 1;
                        DataMgr.setSnowballHitCount(name, 0);
                    }
                
                org.bukkit.block.data.BlockData bd = DataMgr.getPlayerData(p).getTeam().getTeamColor().getWool().createBlockData();
                for (Player o_player : Main.getPlugin().getServer().getOnlinePlayers()) {
                    if(DataMgr.getPlayerData(o_player).getSettings().ShowEffect_MainWeaponInk())
                        if(o_player.getWorld() == inkball.getWorld())
                            if(o_player.getLocation().distanceSquared(inkball.getLocation()) < Main.PARTICLE_RENDER_DISTANCE_SQUARED)
                                o_player.spawnParticle(org.bukkit.Particle.BLOCK_DUST, inkball.getLocation(), 1, 0, 0, 0, 1, bd);
                }

                if(i >= tick && !inkball.isDead()){
                    //半径
                    double maxDist = data.getWeaponClass().getMainWeapon().getBlasterExHankei();
                    
                    //爆発音
                    player.getWorld().playSound(inkball.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1, 1);
                    
                    //爆発エフェクト
                    Sclat.createInkExplosionEffect(inkball.getLocation(), maxDist, 25, player);
                    
                    //バリアをはじく
                    Sclat.repelBarrier(inkball.getLocation(), maxDist, player);
                    
                    //塗る
                    for(int i = 0; i <= maxDist - 1; i++){
                        List<Location> p_locs = Sphere.getSphere(inkball.getLocation(), i, 20);
                        for(Location loc : p_locs){
                            PaintMgr.Paint(loc, p, false);
                            PaintMgr.PaintHightestBlock(loc, p, false, false);
                        }
                    }
                    
                    
                    
                    //攻撃判定の処理
                    for (Entity as : player.getWorld().getEntities()) {
                        if (as instanceof ArmorStand) {
                            if (as.getCustomName() != null) {
                                if (as.getLocation().distanceSquared(ball.getLocation()) <= maxDist*maxDist) {
                                    try {
                                        if (as.getCustomName().equals("Kasa")) {
                                            KasaData kasaData = DataMgr.getKasaDataFromArmorStand((ArmorStand) as);
                                            if (DataMgr.getPlayerData(kasaData.getPlayer()).getTeam() != DataMgr.getPlayerData(p).getTeam()) {
                                                inkball.remove();
                                                cancel();
                                            }
                                        } else if (as.getCustomName().equals("SplashShield")) {
                                            SplashShieldData splashShieldData = DataMgr.getSplashShieldDataFromArmorStand((ArmorStand) as);
                                            if (DataMgr.getPlayerData(splashShieldData.getPlayer()).getTeam() != DataMgr.getPlayerData(p).getTeam()) {
                                                inkball.remove();
                                                cancel();
                                            }
                                        }
                                    }catch (Exception e){}
                                }
                            }
                        }
                    }
               
                    for (Player target : Main.getPlugin().getServer().getOnlinePlayers()) {
                        if(!DataMgr.getPlayerData(target).isInMatch())
                            continue;
                        if (target.getLocation().distance(inkball.getLocation()) <= maxDist + 1) {
                            double damage = 10;
                            if(data.getWeaponClass().getMainWeapon().getIsManeuver())
                                damage = data.getWeaponClass().getMainWeapon().getBlasterExDamage();
                            else
                                damage = (maxDist + 1 - target.getLocation().distance(inkball.getLocation())) * data.getWeaponClass().getMainWeapon().getBlasterExDamage();
                            if(DataMgr.getPlayerData(player).getTeam() != DataMgr.getPlayerData(target).getTeam() && target.getGameMode().equals(GameMode.ADVENTURE)){
                                Sclat.giveDamage(player, target, damage, "killed");

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
                        if(as instanceof ArmorStand){
                            if (as.getLocation().distanceSquared(inkball.getLocation()) <= (maxDist + 1)*(maxDist + 1)) {
                                double damage = (maxDist + 1 - as.getLocation().distance(inkball.getLocation())) * data.getWeaponClass().getMainWeapon().getBlasterExDamage();
                                ArmorStandMgr.giveDamageArmorStand((ArmorStand)as, damage, p);
                            }
                        }
                    }
    
                    inkball.remove();
                }
                if(i != tick)
                    PaintMgr.PaintHightestBlock(inkball.getLocation(), p, false, true);
                if(inkball.isDead())
                    cancel();
                i++;
            }
        };
        task.runTaskTimer(Main.getPlugin(), 0, 1);
    }
    
}
